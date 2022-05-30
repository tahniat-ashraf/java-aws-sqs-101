package com.priyam.sqs.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;

import java.net.URI;

@Order(2)
@Configuration
@Slf4j
public class QueueConfiguration {

    public static final String SQS_ENDPOINT = "http://sqs.host:4566";

    private final String activeProfile;

    public QueueConfiguration(
            @Qualifier("activeProfile") String activeProfile
    ) {
        log.info("=> constructor QueueConfiguration()");
        this.activeProfile = activeProfile;
    }


    @Bean
    @Qualifier("queueUrl")
    public String queueUrl() {

        log.info("=> queueUrl()");

        try {
            return getQueueUrl();
        } catch (QueueDoesNotExistException queueDoesNotExistException) {
            log.error("Queue doesn't exist. Creating the queue now ...");
            var createQueueRequest = CreateQueueRequest.builder()
                    .queueName(activeProfile + "-priyam-fifo-queue.fifo")
                    .build();

            sqsClient().createQueue(createQueueRequest);
            return getQueueUrl();
        }

    }

    private String getQueueUrl() {
        var getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName(activeProfile + "-priyam-fifo-queue.fifo")
                .build();

        return sqsClient().getQueueUrl(getQueueUrlRequest).queueUrl();
    }

    @Bean
    public SqsClient sqsClient() {

        log.info("=> setup()");

        if (activeProfile.compareTo("dev") == 0) {
            return SqsClient.builder()
                    .endpointOverride(URI.create(SQS_ENDPOINT))
                    .build();
        }

        return SqsClient.create();

    }


}
