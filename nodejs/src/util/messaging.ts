import { SendMessageCommandOutput, SQS } from "@aws-sdk/client-sqs";

const REGION = process.env["AWS_REGION"];

const sqs = new SQS({
  region: REGION,
});

export async function sendMessageToSQSQueue(
  queueUrl: string,
  messageBody: object,
  otherParams: object = {}
): Promise<SendMessageCommandOutput> {
  console.log(
    `Sending the following message to SQS queue "${queueUrl}": "${JSON.stringify(
      messageBody
    )}" with additional params "${JSON.stringify(otherParams)}"`
  );
  const params = {
    QueueUrl: queueUrl,
    MessageBody: JSON.stringify(messageBody),
    ...otherParams,
  };
  return sqs.sendMessage(params);
}
