package com.priyam.sqs.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.priyam.sqs.model.PublishSqsMessageRequest;
import com.priyam.sqs.model.ReceiveMessageResponse;
import com.priyam.sqs.service.SqsConsumerService;
import com.priyam.sqs.service.SqsPublisherService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sqs/message")
public class SqsMessageController {

    private final SqsPublisherService sqsPublisherService;

    private final SqsConsumerService consumerServiceA;

    private final SqsConsumerService consumerServiceB;

    private final SqsConsumerService consumerServiceC;


    public SqsMessageController(
            SqsPublisherService sqsPublisherService,
            @Qualifier("consumerA") SqsConsumerService consumerServiceA,
            @Qualifier("consumerB") SqsConsumerService consumerServiceB,
            @Qualifier("consumerC") SqsConsumerService consumerServiceC) {
        this.sqsPublisherService = sqsPublisherService;
        this.consumerServiceA = consumerServiceA;
        this.consumerServiceB = consumerServiceB;
        this.consumerServiceC = consumerServiceC;
    }

    @PostMapping
    public void publishMessage(@RequestBody PublishSqsMessageRequest request) throws JsonProcessingException {
        sqsPublisherService.publishMessage(request);
    }

    @GetMapping("/a")
    public List<ReceiveMessageResponse> receiveMessageA() {
        return consumerServiceA.receiveMessage();
    }

    @GetMapping("/b")
    public List<ReceiveMessageResponse> receiveMessageB() {
        return consumerServiceB.receiveMessage();
    }

    @GetMapping("/c")
    public List<ReceiveMessageResponse> receiveMessageC() {
        return consumerServiceC.receiveMessage();
    }
}
