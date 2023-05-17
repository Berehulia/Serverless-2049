package com.lineup.scenario03.function.java;

import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.concurrent.Callable;

public class BatchMessageSender implements Callable {

    private final List<SendMessageBatchRequestEntry> entries;

    public BatchMessageSender(List<SendMessageBatchRequestEntry> entries) {
        this.entries = entries;
    }

    @Override
    public Object call() throws Exception {
        SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder()
                .entries(entries)
                .queueUrl(GeneratorFunction.SQS_NAME)
                .build();
        GeneratorFunction.SQS_CLIENT.sendMessageBatch(sendMessageBatchRequest);
        return null;
    }
}
