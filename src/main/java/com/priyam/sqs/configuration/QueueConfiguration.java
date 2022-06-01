package com.priyam.sqs.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            var queueUrl = getQueueUrl();
            printAttributesOfQueue(queueUrl);
            return queueUrl;
        } catch (QueueDoesNotExistException queueDoesNotExistException) {
            log.error("Queue doesn't exist. Creating the queue now ...");

            // Enable long polling when creating a queue.
            HashMap<QueueAttributeName, String> attributes = new HashMap<>();
            attributes.put(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20");
            attributes.put(QueueAttributeName.VISIBILITY_TIMEOUT, "60");
            attributes.put(QueueAttributeName.FIFO_QUEUE, "true");
            attributes.put(QueueAttributeName.CONTENT_BASED_DEDUPLICATION,"true");


            var createQueueRequest = CreateQueueRequest.builder()
                    .queueName(activeProfile + "-priyam-fifo-queue.fifo")
                    .attributes(attributes)
                    .build();

            sqsClient().createQueue(createQueueRequest);
            return getQueueUrl();
        }

    }

    private void printAttributesOfQueue(String queueUrl) {

        log.info("=> printAttributesOfQueue()");

        var atts = new ArrayList<QueueAttributeName>();
        atts.add(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES);
        atts.add(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS);
        atts.add(QueueAttributeName.VISIBILITY_TIMEOUT);
        atts.add(QueueAttributeName.FIFO_QUEUE);
        atts.add(QueueAttributeName.CONTENT_BASED_DEDUPLICATION);

        var attributesRequest= GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(atts)
                .build();

        GetQueueAttributesResponse response = sqsClient().getQueueAttributes(attributesRequest);

        var queueAtts = response.attributesAsStrings();
        for (Map.Entry<String,String> queueAtt : queueAtts.entrySet())
            System.out.println("Key = " + queueAtt.getKey() +
                    ", Value = " + queueAtt.getValue());
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
