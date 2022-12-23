import AdmZip, { IZipEntry } from "adm-zip";
import { PutObjectCommandOutput } from "@aws-sdk/client-s3";

import { S3 } from "@aws-sdk/client-s3";

const s3 = new S3({});

export async function readFromS3(
  bucketName: string,
  objectKey: string
): Promise<Uint8Array> {
  console.log(`Reading data from S3: ${bucketName}/${objectKey}`);
  const data = await s3.getObject({
    Bucket: bucketName,
    Key: objectKey,
  });
  return data.Body.transformToByteArray();
}

export async function saveToS3(
  data: Uint8Array,
  bucketName: string,
  objectKey: string
): Promise<PutObjectCommandOutput> {
  console.log(
    `Uploading ${data.length} bytes to S3 bucket ${bucketName}/${objectKey}`
  );
  return s3.putObject({
    Bucket: bucketName,
    Key: objectKey,
    Body: data,
  });
}

/**
 * Reads a compressed array of bytes, unzips it, finds the first entry, reads it and returns an array of strings splitting them by the new line escape sequence.
 * Throws an exception if the provided zip archive has more than one entry or its entry is not a JSON lines file.
 *
 * @param byteArray compressed array of bytes representing a zip archive
 */
export function unzipJson(byteArray: Uint8Array): [string, Buffer] {
  console.log("Starting unzipping an archive");
  const zip = new AdmZip(Buffer.from(byteArray));
  const zipEntries = zip.getEntries();

  if (zipEntries.length > 1)
    throw new Error(
      `Unexpected number of files in a zip archive, expecting only one JSON lines file`
    );

  const firstZipEntry: IZipEntry = zipEntries.at(0);
  console.log(`Reading ${firstZipEntry.entryName} from the zip archive`);

  if (!hasCorrectExtension(firstZipEntry, ".jsonl"))
    throw new Error(
      `Expecting JSON lines file "${firstZipEntry.entryName}" does not have an expected extension - .jsonl`
    );

  return [firstZipEntry.entryName, firstZipEntry.getData()];
}

function hasCorrectExtension(
  firstZipEntry: AdmZip.IZipEntry,
  expectedExtension: string
) {
  return firstZipEntry.entryName.toLowerCase().endsWith(expectedExtension);
}
