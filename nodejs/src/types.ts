/**
 * Represent an SQS message format for UnpackFunction.
 */
interface UnpackFunctionMessage {
  fileId: string;
  archiveInputKey: string;
}

/**
 * Represents an SQS message format that is sent to ValidateFilePartFunction.
 */
interface UnpackArchiveResponse {
  fileId: string;
  fileInputKey: string;
  fileParts: FilePart[];
}

interface FilePart {
  filePartId: number;
  indexStart: number;
  indexEnd: number;
}
