package com.lineup.scenario01.function.java;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Function
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
{

  private static final DynamoDbClient DYNAMO_DB_CLIENT = buildDynamoDbClient();

  private static final String TABLE_NAME = Objects.requireNonNull(System.getenv("TABLE_NAME"));

  private static DynamoDbClient buildDynamoDbClient() {

    return DynamoDbClient
        .builder()
        .httpClient(UrlConnectionHttpClient.create())
        .region(getRegion())
        .credentialsProvider(getCredentialsProvider())
        .build();
  }

  private static Region getRegion() {

    return Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable()));
  }

  private static AwsCredentialsProvider getCredentialsProvider() {

    if (SdkSystemSetting.AWS_CONTAINER_CREDENTIALS_FULL_URI.getStringValue().isPresent()) {

      return ContainerCredentialsProvider.builder().build();
    }

    return EnvironmentVariableCredentialsProvider.create();
  }

  @Override
  public APIGatewayProxyResponseEvent handleRequest(
      APIGatewayProxyRequestEvent event,
      Context context
  ) {

    insertRecord();

    return new APIGatewayProxyResponseEvent()
        .withStatusCode(200);
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