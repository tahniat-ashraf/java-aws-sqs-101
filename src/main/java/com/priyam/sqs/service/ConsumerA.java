package com.priyam.sqs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.priyam.sqs.model.MyMessage;
import com.priyam.sqs.model.ReceiveMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityBatchRequest;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.ArrayList;
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
//                .visibilityTimeout(60)
                .build();

//        final List<ChangeMessageVisibilityBatchRequestEntry> entries = new ArrayList<>();


        var responseList = sqsClient.receiveMessage(receiveMsgRequest)
                .messages()
                .stream()
                .map(message -> {
                            log.info("message {}", message);

                            message.attributes()
                                    .forEach((messageSystemAttributeName, s) -> log.info("messageSystemAttributeName, s {},{}", messageSystemAttributeName, s));


                            message.attributesAsStrings()
                                    .forEach((s, s2) -> log.info("s,s2 {},{}", s, s2));

//                            entries.add(ChangeMessageVisibilityBatchRequestEntry.builder()
//                                    .id(message.messageId())
//                                    .receiptHandle(message.receiptHandle())
//                                    .visibilityTimeout(60)
//                                    .build());

                            return ReceiveMessageResponse.builder()
                                    .messageId(message.messageId())
                                    .message(gson.fromJson(message.body(), MyMessage.class))
                                    .receiptHandle(message.receiptHandle())
                                    .approximateReceiveCount(message.attributesAsStrings().get("ApproximateReceiveCount"))
                                    .approximateFirstReceiveTimestamp(message.attributesAsStrings().get("ApproximateFirstReceiveTimestamp"))
                                    .messageDeduplicationId(message.attributesAsStrings().get("MessageDeduplicationId"))
                                    .messageGroupId(message.attributesAsStrings().get("MessageGroupId"))
                                    .senderId(message.attributesAsStrings().get("SenderId"))
                                    .sentTimestamp(message.attributesAsStrings().get("SentTimestamp"))
                                    .sequenceNumber(message.attributesAsStrings().get("SequenceNumber"))
                                    .build();
                        }
                ).collect(Collectors.toList());


//        var changeMessageVisibilityBatchRequest = ChangeMessageVisibilityBatchRequest.builder()
//                .queueUrl(queueUrl)
//                .entries(entries)
//                .build();
//
//        sqsClient.changeMessageVisibilityBatch(changeMessageVisibilityBatchRequest);

        return responseList;
    }
}
