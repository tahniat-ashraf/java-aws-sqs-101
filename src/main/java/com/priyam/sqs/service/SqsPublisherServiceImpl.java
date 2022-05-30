package com.priyam.sqs.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.priyam.sqs.model.PublishSqsMessageRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;


@Service
public class SqsPublisherServiceImpl implements SqsPublisherService {

    private final SqsClient sqsClient;

    private final String queueUrl;

    private final ObjectMapper mapper;

    public SqsPublisherServiceImpl(

            SqsClient sqsClient,
            ObjectMapper mapper,
            @Qualifier("queueUrl") String queueUrl) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.mapper = mapper;
    }

    public SendMessageResponse publishMessage(PublishSqsMessageRequest request) throws JsonProcessingException {


        var message = mapper.writeValueAsString(request.getMessage());

        var sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .messageDeduplicationId(request.getMessage().getId())
                .messageGroupId(request.getMessageGroupId())
                .build();

        return sqsClient.sendMessage(sendMsgRequest);

    }

}


