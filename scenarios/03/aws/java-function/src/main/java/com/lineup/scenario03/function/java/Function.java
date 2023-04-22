package com.lineup.scenario03.function.java;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Function implements RequestHandler<SQSEvent, String> {

    private static final DynamoDbClient DYNAMO_DB_CLIENT = DynamoDbClient.create();

    private static final String TABLE_NAME = Objects.requireNonNull(System.getenv("TABLE_NAME"));

    @Override
    public String handleRequest(SQSEvent sqsEvent, Context context) {
        System.out.println("Hello Java with insertRecord");
        insertRecord();
        return "Hello Java with insertRecord";
    }

    private void insertRecord() {

        DYNAMO_DB_CLIENT.putItem(
                PutItemRequest
                        .builder()
                        .tableName(TABLE_NAME)
                        .item(
                                Map.of(
                                        "id",
                                        AttributeValue.fromS(UUID.randomUUID().toString())
                                )
                        )
                        .build()
        );
    }
}