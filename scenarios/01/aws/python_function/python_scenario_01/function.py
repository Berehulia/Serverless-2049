import json
import logging
import os
import uuid
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger()
logger.setLevel(logging.INFO)

# Recommended optimization - initializing clients in global scope
dynamodb_client = boto3.client("dynamodb", region_name=os.environ['AWS_REGION'])

def lambda_handler(event, context) -> dict:
    """
    Generates a random UUID and saves it to a DynamoDB table.

    Parameters
    ----------
    event: dict, required
        API Gateway Lambda Proxy Input Format

        Event doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format

    context: object, required
        Lambda Context runtime methods and attributes

        Context doc: https://docs.aws.amazon.com/lambda/latest/dg/python-context-object.html

    Returns
    ------
        API Gateway Lambda Proxy Output Format: dict

        Return doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html
    """
    if 'DYNAMODB_TABLE_NAME' not in os.environ:
        logger.fatal('DYNAMODB_TABLE_NAME should be set as an environment variable')
        raise RuntimeError('DYNAMODB_TABLE_NAME environment variable should be set')

    random_uuid = str(uuid.uuid4())
    save_uuid(random_uuid, os.environ['DYNAMODB_TABLE_NAME'])

    return {
        "statusCode": 200,
        "body": json.dumps({
            "id": random_uuid,
        }),
    }


def save_uuid(id: str, table_name: str) -> type(None):
    """
    Saves uuid to a DynamoDB table.
    The table should have id as a primary key.

    :param id: UUID as a string
    :param table_name: DynamoDB to save the uuid
    :return:
    """
    logger.info('Saving "%s" as id to the "%s" table', id, table_name)
    try:
        dynamodb_client.put_item(
            TableName=table_name,
            Item={'id':
                      {'S': id} # Specify type for id (String)
                  }
        )
    except ClientError as err:
        logger.fatal(
            "Couldn't save uuid %s to table %s. Reason: %s: %s",
            id, table_name,
            err.response['Error']['Code'], err.response['Error']['Message'])
        raise
    logger.info('Successfully saved id "%s" to the "%s" table', id, table_name)
