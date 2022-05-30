package com.priyam.sqs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.priyam.sqs.model.PublishSqsMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

public interface SqsPublisherService {

    SendMessageResponse publishMessage(PublishSqsMessageRequest request) throws JsonProcessingException;
}
