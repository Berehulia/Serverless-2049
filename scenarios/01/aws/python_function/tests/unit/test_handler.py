import re
import boto3
import json
import os
import botocore
import pytest
from moto import mock_dynamodb


@pytest.fixture(scope="function")
def aws_credentials():
    """Mocked AWS Credentials for moto."""
    os.environ["AWS_ACCESS_KEY_ID"] = "testing"
    os.environ["AWS_SECRET_ACCESS_KEY"] = "testing"
    os.environ["AWS_SECURITY_TOKEN"] = "testing"
    os.environ["AWS_SESSION_TOKEN"] = "testing"
    os.environ["AWS_DEFAULT_REGION"] = "us-east-1"
    os.environ["AWS_REGION"] = "us-east-1"

@pytest.fixture(scope="function")
def dynamodb_client(aws_credentials):
    with mock_dynamodb():
        client = boto3.client("dynamodb")
        client.create_table(
            TableName='table_name',
            AttributeDefinitions=[
                {"AttributeName": "id", "AttributeType": "S"},
            ],
            KeySchema=[
                {"AttributeName": "id", "KeyType": "HASH"},
            ],
            BillingMode='PAY_PER_REQUEST')
        yield client


def test_lambda_handler_saves_uuid(dynamodb_client):
    from python_scenario_01 import function
    os.environ['DYNAMODB_TABLE_NAME'] = 'table_name'

    response = function.lambda_handler({}, "")

    response_body = json.loads(response["body"])
    saved_item = dynamodb_client.get_item(
        TableName='table_name',
        Key={
            'id': {'S': response_body['id']}
        }
    )
    assert response["statusCode"] == 200
    assert "id" in response["body"]
    assert response_body['id'] == saved_item['Item']['id']['S']


def test_lambda_handler_throws_table_name_not_set(dynamodb_client, caplog):
    from python_scenario_01 import function
    del os.environ['DYNAMODB_TABLE_NAME']

    with pytest.raises(RuntimeError, match='DYNAMODB_TABLE_NAME environment variable should be set'):
        function.lambda_handler({}, "")
    assert 'DYNAMODB_TABLE_NAME should be set as an environment variable' in caplog.text


def test_lambda_handler_throws_dynamodb_client_error(dynamodb_client, caplog):
    from python_scenario_01 import function
    os.environ['DYNAMODB_TABLE_NAME'] = 'none_existing_table'

    with pytest.raises(botocore.exceptions.ClientError):
        function.lambda_handler({}, "")
    log_message_regex = re.compile(r"Couldn't save uuid .*? to table none_existing_table\. "
                                   "Reason: ResourceNotFoundException: Requested resource not found")
    assert log_message_regex.search(caplog.text)