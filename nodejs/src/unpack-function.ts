import { Context, SQSEvent } from "aws-lambda";
import { readFromS3, saveToS3, unzipJson } from "./util/storage";
import { sendMessageToSQSQueue } from "./util/messaging";

const MAX_PART_SIZE: number =
  Number(process.env["MAX_PART_SIZE"]) || 10_485_760; // 10MB
type MessageHandler = () => void;

/**
 * Reads a zip archive from USER_BUCKET, unzips it, saves it in SYSTEM_BUCKET and splits it in parts to send a message for processing by the next lambda function.
 *
 * @param event sent from SQS
 * @param context handler function context
 */
export async function unpackArchiveFunctionHandler(
  event: SQSEvent,
  context: Context
) {
  const USER_BUCKET = readEnvVariableOrThrowErrorIfNotExist("USER_BUCKET");
  const SYSTEM_BUCKET = readEnvVariableOrThrowErrorIfNotExist("SYSTEM_BUCKET");
  const SOS_QUEUE_URL_TO_SEND_MESSAGES = readEnvVariableOrThrowErrorIfNotExist(
    "SOS_QUEUE_URL_TO_SEND_MESSAGES"
  );

  const functions: MessageHandler[] = event.Records.map((record, _) => {
    return async () => {
      // 0. Serialize a message body to JSON
      console.log("Serializing a message body to JSON");
      const message: UnpackFunctionMessage = JSON.parse(record.body);
      console.log(`Successfully serialized the message: ${message}`);

      // 1. Download from User Bucket
      console.log(
        `Downloading zip archive from ${USER_BUCKET}/${message.archiveInputKey}`
      );
      const archiveByteArray = await readFromS3(
        USER_BUCKET,
        message.archiveInputKey
      );
      console.log(
        `Successfully downloaded zip archive from ${USER_BUCKET}/${message.archiveInputKey}`
      );

      // 2. Unzip stream
      console.log("Unzipping an archive downloaded from S3");
      const [jsonFileName, unzippedArray] = unzipJson(archiveByteArray);
      console.log(
        `Successfully unzipped the "${jsonFileName}" file from the archive`
      );

      // 3. Split byte array into parts
      console.log(
        "Splitting file into multiple parts to prepare an SQS message"
      );
      const sqsMessage = buildResponseMessage(
        message,
        jsonFileName,
        unzippedArray
      );
      console.log(
        `Successfully split the file into ${sqsMessage.fileParts.length} parts and prepared an SQS message`
      );

      // 4. Save unzipped array
      console.log(
        `Saving unzipped archive to S3 bucket ${SYSTEM_BUCKET}/${jsonFileName}`
      );
      await saveToS3(unzippedArray, SYSTEM_BUCKET, jsonFileName);
      console.log(
        `Successfully saved data to S3 to ${SYSTEM_BUCKET}/${jsonFileName}`
      );

      // 5. Sent a message to SQS Queue for Validation function
      console.log(
        `Sending the prepared message to SQS queue ${SOS_QUEUE_URL_TO_SEND_MESSAGES}`
      );
      await sendMessageToSQSQueue(SOS_QUEUE_URL_TO_SEND_MESSAGES, sqsMessage);
      console.log(
        `Successfully sent the prepared message to SQS queue ${SOS_QUEUE_URL_TO_SEND_MESSAGES}`
      );

      return JSON.stringify(message); // return processed message for logging purposes
    };
  });

  let areErrorsEncountered = false;
  for (const func of functions) {
    try {
      const msg = await func();
      console.log(`Successfully processed message "${msg}"`);
    } catch (e) {
      areErrorsEncountered = true;
      console.error(
        `Encountered an unexpected error during processing - "${e}"`
      );
    }
  }

  if (areErrorsEncountered)
    throw new Error(
      "There were some errors while processing the request. Please check logs for more details."
    );
}

function buildResponseMessage(
  message: UnpackFunctionMessage,
  jsonFileName: string,
  unzippedArray: Buffer
) {
  const sqsMessage: UnpackArchiveResponse = {
    fileId: message.fileId,
    fileInputKey: jsonFileName,
    fileParts: [],
  };
  let partSize = 0;
  let partNumber = 0;

  let startIndex = 0;
  let finalIndex = -1;

  let lineSize = 0;
  for (const b of unzippedArray) {
    if (b !== "\n".charCodeAt(0)) {
      lineSize++;
    } else {
      partSize += lineSize;
    }

    if (isFullPart(partSize)) {
      finalIndex += partSize;

      sqsMessage.fileParts.push({
        filePartId: partNumber,
        indexStart: startIndex,
        indexEnd: finalIndex,
      });
      startIndex += partSize;
      partSize = 0;
      partNumber++;
    }
  }
  return sqsMessage;
}

function isFullPart(partSize: number) {
  return partSize > MAX_PART_SIZE;
}

function readEnvVariableOrThrowErrorIfNotExist(
  environmentVariableName: string
) {
  const envVariable = process.env[environmentVariableName];
  if (!envVariable)
    throw new Error(`${envVariable} env variable should be set`);
  return envVariable;
}
