import boto3
import os
import uuid

dyn_resource = boto3.resource('dynamodb')
table = dyn_resource.Table(os.getenv('TABLE_NAME'))

def lambda_handler(event, context):
    print("Hello Python with insertRecord")
    insertRecord()
    return "Hello Python with insertRecord"

def insertRecord():
    id = str(uuid.uuid4())
    table.put_item(Item={
        'id': id
    })
    