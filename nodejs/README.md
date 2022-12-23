# Serverless 2049 - nodejs
What is Serverless 2049?

It is a project to measure performance and runtime characteristics of Lambda functions written in different languages.

This repo contains an implementation written with [Node.js](https://nodejs.org/).  

## Build
Source code is located in `src/` directory, and it is written in TypeScript. 
To be able to run on AWS we need to compile it to JavaScript using the following command (from the root directory):
```
tsc
```
Compiled `.js` files will be placed in `build` directory

## Testing
The code was manually tested on `Node.js 18.x`

### SQS message formats
You can find message format examples below that can be used for testing.

#### UnpackArchiveFunction

Single message body:
```json
{
  "fileId": "1",
  "archiveInputKey": "on-prem-suspects-1MB-json.zip"
}
```
SQS message:
```json
{
  "Records": [
    {
      "messageId": "19dd0b57-b21e-4ac1-bd88-01bbb068cb78",
      "receiptHandle": "MessageReceiptHandle",
      "body": "{\"fileId\":\"1\",\"archiveInputKey\":\"on-prem-suspects-1MB-json.zip\"}",
      "attributes": {
        "ApproximateReceiveCount": "1",
        "SentTimestamp": "1523232000000",
        "SenderId": "123456789012",
        "ApproximateFirstReceiveTimestamp": "1523232000001"
      },
      "messageAttributes": {},
      "md5OfBody": "{{{md5_of_body}}}",
      "eventSource": "aws:sqs",
      "eventSourceARN": "arn:aws:sqs:us-east-1:123456789012:MyQueue",
      "awsRegion": "us-east-1"
    }
  ]
}
```

## Environment variables
#### UnpackArchiveFunction
Required environment variables:
- `USER_BUCKET`
- `SYSTEM_BUCKET`
- `SOS_QUEUE_URL_TO_SEND_MESSAGES` - is used to send messages that will trigger an execution of other functions

Optional environment variables:
- `MAX_PART_SIZE`

# Deployment
At the moment we don't have an automated deployment process, the deployment should be done manually.

Please follow steps below to deploy the lambdas:
1. Create required resources in AWS console (AWS lambdas, S3 buckets, SQS queues, IAM roles with required permissions)
2. Create the Lambda layer using AWS Cloud9 (please follow the instructions below)
3. build the code (please refer to the `Build` section in this document)
4. zip the code from step 3
5. Upload the zip archive with built code to created lambda from step 1
6. Update Lambda config from step 1 to your target settings (do not forget to update environment variables, custom layer from step 2 etc.)

## Creating Lambda layer using AWS Cloud9 
More details: https://aws.amazon.com/blogs/compute/using-lambda-layers-to-simplify-your-development-process/

### Steps to deploy

1 step - Create environment and open it in [AWS Cloud9 console](https://console.aws.amazon.com/cloud9/home)

2 step - Create `packages.json` from this repo
```
touch package.json
```

3 step - Install dependencies - `npm install --omit=dev`

4 step - Move installed dependencies them to `/layer/nodejs`
```
mkdir -p ./layer/nodejs
mv ./node_modules ./layer/nodejs
```

5 step - Deploy the layer with AWS Serverless Application Model (AWS SAM) - `sam deploy --guided` using the following template:
```
touch template.yaml
```
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31
Description: AWS SDK Layer

Resources:
  SDKlayer:
    Type: AWS::Serverless::LayerVersion
    Properties:
      LayerName: ETL-functions-dependencies
      Description: adm-zip and AWS clients.
      ContentUri: './layer'
      CompatibleRuntimes:
        - nodejs18.x
      LicenseInfo: ''
      RetentionPolicy: Retain
```

## TODOs
### TODO: consider using top level await to improve performance
Use ES modules and top level await to improve performance - https://aws.amazon.com/blogs/compute/using-node-js-es-modules-and-top-level-await-in-aws-lambda/

## Side Notes
Lambda runtime does not contain AWS SDK for JavaScript v3, thus we need to package it with our function, 
read the bug for more details - https://github.com/aws/aws-sdk-js-v3/issues/3230