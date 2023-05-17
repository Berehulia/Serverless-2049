package com.lineup.scenario03.function.java;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.awssdk.regions.Region.US_WEST_2;

public class GeneratorFunction implements RequestHandler<String, String> {
    public static final String SQS_NAME = System.getenv("SQS_NAME");
    public static final SqsClient SQS_CLIENT = SqsClient.create();
    public static List<SendMessageBatchRequestEntry> entries =
            Stream.generate(() -> SendMessageBatchRequestEntry.builder()
                            .messageBody("{}")
                            .id(UUID.randomUUID().toString())
                            .build())
                    .limit(10)
                    .collect(Collectors.toList());


    @Override
    public String handleRequest(String input, Context context) {

        Integer count = Integer.valueOf(input);
        System.out.println("Start generating " + count + " message batches");

        List<Callable<Object>> callableList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            callableList.add(new BatchMessageSender(entries));
        }
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            List<Future<Object>> futures;
            futures = executorService.invokeAll(callableList);
            for (Future<Object> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Finish generating " + count + " message batches");
        return "Finish generating messages";
    }
}