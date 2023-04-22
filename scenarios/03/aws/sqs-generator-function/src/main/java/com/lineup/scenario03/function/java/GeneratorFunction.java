package com.lineup.scenario03.function.java;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GeneratorFunction implements RequestHandler<String, String> {
    public static final String SQS_NAME = System.getenv("SQS_NAME");
    public static final SqsClient SQS_CLIENT = SqsClient.create();


    @Override
    public String handleRequest(String input, Context context) {

        Integer count = Integer.valueOf(input);
        System.out.println("Start generating " + count + " messages");

        List<Callable<Object>> callableList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            callableList.add(new MessageSender());
        }
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        try {
            List<Future<Object>> futures;
            futures = executorService.invokeAll(callableList);
            for (Future<Object> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Finish generating " + count + " messages");
        return "Finish generating messages";
    }
}