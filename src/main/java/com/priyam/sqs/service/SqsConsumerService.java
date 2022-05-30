package com.priyam.sqs.service;

import com.priyam.sqs.model.ReceiveMessageResponse;

import java.util.List;

public interface SqsConsumerService {

    List<ReceiveMessageResponse> receiveMessage();
}
