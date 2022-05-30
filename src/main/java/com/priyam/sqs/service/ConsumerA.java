package com.priyam.sqs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.priyam.sqs.model.MyMessage;
import com.priyam.sqs.model.ReceiveMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service("consumerA")
@Slf4j
public class ConsumerA implements SqsConsumerService {

    private final SqsClient sqsClient;

    private final String queueUrl;

    private final Gson gson;

    public ConsumerA(

            SqsClient sqsClient,
            Gson gson,
            @Qualifier("queueUrl") String queueUrl) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.gson = gson;
    }

    @Override
    public List<ReceiveMessageResponse> receiveMessage() {
                var receiveMsgRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(3)
                .visibilityTimeout(60)
                .build();

         return sqsClient.receiveMessage(receiveMsgRequest).messages()
                 .stream()
                 .map(message -> {
                     log.info("message {}",message);
                             return ReceiveMessageResponse.builder()
                                     .messageId(message.messageId())
                                     .message(gson.fromJson(message.body(), MyMessage.class))
                                     .receiptHandle(message.receiptHandle())
                                     .build();
                         }
                 ).collect(Collectors.toList());
    }
}
