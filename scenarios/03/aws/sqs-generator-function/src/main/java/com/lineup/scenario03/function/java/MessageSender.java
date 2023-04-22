package com.lineup.scenario03.function.java;

import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.concurrent.Callable;

public class MessageSender implements Callable {

    @Override
    public Object call() throws Exception {
        GeneratorFunction.SQS_CLIENT.sendMessage(SendMessageRequest.builder()
                .queueUrl(GeneratorFunction.SQS_NAME)
                .messageBody("{}")
                .build()
        );
        return null;
    }
}
