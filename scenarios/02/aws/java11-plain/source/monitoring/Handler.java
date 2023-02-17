package com.monitoring;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.monitoring.auth.Credentials;
import com.monitoring.model.Monitoring;
import com.monitoring.model.RouterInfo;
import com.monitoring.service.PresignedUrlSample;
import com.monitoring.service.SecretService;
import com.monitoring.service.ping.PingProviderRegistry;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.ArrayList;
import java.util.List;

public class Handler implements RequestHandler<Object, String> {


    private static final String BUCKET_NAME = "monitoring-2022";
    private static final String REGION = "us-west-2";


    private static AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
            .withRegion(REGION)
            .build();
    private static DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
    private static AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
            .withRegion(REGION)
            .build();

    private static SnsClient snsClient = SnsClient
            .builder()
            .region(Region.US_WEST_2)
            .build();

    private static PingProviderRegistry pingProviderRegistry = new PingProviderRegistry();

    public static void main(String[] args) {
        new Handler().handleRequest(null, null);
    }

    public String handleRequest(Object event, Context context) {
        LocalDateTime now = LocalDateTime.now(DateTimeZone.forID("Europe/Kiev"));
        String date = now.toString(DateTimeFormat.forPattern("yyyy-MM-dd"));

        List<RouterInfo> routerInfos = getRouters();
        for (RouterInfo routerInfo : routerInfos) {
            String pk = date + "-" + routerInfo.getName();
            String status = pingProviderRegistry.getByName(routerInfo.getPingProvider())
                    .getStatus(routerInfo.getIp(), routerInfo.getTimeOutMS());
            List<Monitoring> monitoringList = getMonitoringForDate(pk);
            Monitoring monitoring;
            String currentDateTime = now.toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"));
            if (monitoringList.size() > 0 && status.equals(monitoringList.get(0).getStatus())) {
                monitoring = monitoringList.get(0);
                monitoring.setTo(currentDateTime);
                monitoring.setChecksCount(monitoring.getChecksCount() + 1);
            } else {
                monitoring = new Monitoring(pk, currentDateTime, currentDateTime, status, 1, routerInfo.getName());
                if (monitoringList.isEmpty()) {
                    monitoringList.add(monitoring);
                } else {
                    List<Monitoring> monitoringListNew = new ArrayList<>();
                    monitoringListNew.add(monitoring);
                    monitoringListNew.addAll(monitoringList);
                    monitoringList = monitoringListNew;
                }
            }
            save(monitoring, monitoringList);
        }

        System.out.println("Done");
        return "Done";
    }

    private void save(Monitoring monitoring, List<Monitoring> monitoringList) {
        dynamoDBMapper.save(monitoring);
        amazonS3.putObject(BUCKET_NAME, monitoring.getDate(), monitoringListToString(monitoringList));
        if (monitoringList.size() == 1 && monitoringList.get(0).getChecksCount() == 1) {
            Credentials credentials = SecretService.getSecret();
            if (credentials == null) {
                throw new RuntimeException("Cannot get creadentials from secret");
            }
            String presignedURL = PresignedUrlSample.getPresignedUrlToS3Object(
                    REGION, BUCKET_NAME, monitoring.getDate(), 60 * 60 * 24 * 7,
                    credentials.getAdminKey(), credentials.getAdminSecret());

            String message = new StringBuilder()
                    .append(monitoring.getDate()).append("\n")
                    .append(presignedURL)
                    .toString();
            PublishRequest publishRequest = PublishRequest.builder()
                    .message(message)
                    .subject(monitoring.getDate())
                    .targetArn("arn:aws:sns:us-west-2:021093800035:monitoring")
                    .build();
            snsClient.publish(publishRequest);
        }
    }

    private String monitoringListToString(List<Monitoring> monitoringList) {
        final StringBuilder stringBuilder = new StringBuilder().append(monitoringList.get(0).getDate()).append('\n');
        monitoringList.forEach(i -> stringBuilder.append(i).append('\n'));
        return stringBuilder.toString();
    }


    private List<RouterInfo> getRouters() {
        DynamoDBQueryExpression<RouterInfo> queryExpression = new DynamoDBQueryExpression<RouterInfo>()
                .withHashKeyValues(new RouterInfo("RouterInfo"));
        return dynamoDBMapper.query(RouterInfo.class, queryExpression);
    }

    private List<Monitoring> getMonitoringForDate(String pk) {
        DynamoDBQueryExpression<Monitoring> queryExpression = new DynamoDBQueryExpression<Monitoring>()
                .withHashKeyValues(new Monitoring(pk))
                .withScanIndexForward(false);

        QueryResultPage<Monitoring> monitoringList = dynamoDBMapper.queryPage(Monitoring.class, queryExpression);

        return monitoringList.getResults();
    }
}
