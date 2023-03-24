package com.lineup.scenario02.function.java;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Function
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
{

  private static final S3Client S3_CLIENT = buildS3Client();

  private static final String BUCKET_NAME = Objects.requireNonNull(System.getenv("BUCKET_NAME"));

  private static final String FILE_NAME = Objects.requireNonNull(System.getenv("FILE_NAME"));

  private static S3Client buildS3Client() {

    return S3Client
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
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

    byte[] file = downloadFile();
    byte[] zip = zipFile(file);
    uploadZip(zip);

    return new APIGatewayProxyResponseEvent()
        .withStatusCode(200);
  }

  private byte[] downloadFile() {

    return S3_CLIENT
        .getObjectAsBytes(
            GetObjectRequest
                .builder()
                .bucket(BUCKET_NAME)
                .key(FILE_NAME)
                .build()
        )
        .asByteArray();
  }

  private byte[] zipFile(byte[] file) {

    try (
        var byteStream = new ByteArrayOutputStream();
        var zipStream = new ZipOutputStream(byteStream)
    ) {

      var entry = new ZipEntry(FILE_NAME);
      zipStream.putNextEntry(entry);
      zipStream.write(file);

      return byteStream.toByteArray();

    } catch (IOException exception) {

      throw new RuntimeException(exception);
    }
  }

  private void uploadZip(byte[] zip) {

    S3_CLIENT.putObject(
        PutObjectRequest
            .builder()
            .bucket(BUCKET_NAME)
            .key("output/" + UUID.randomUUID() + ".zip")
            .build(),
        RequestBody.fromBytes(zip)
    );
  }

}