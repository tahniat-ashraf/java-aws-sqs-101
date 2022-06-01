package com.priyam.sqs.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import java.util.ArrayList;
import java.util.Map;

public abstract class AbstractConsumer {


    private final SqsClient sqsClient;

    private final String queueUrl;

    private final Gson gson;

    private final Logger logger;

    public AbstractConsumer(
            SqsClient sqsClient,
            Gson gson,
            String queueUrl,
            Logger logger
    ){
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.gson = gson;
        this.logger = logger;
    }

    protected void getQueueItemCount(){

        logger.info("=> getQueueItemCount");

        var atts = new ArrayList<QueueAttributeName>();
        atts.add(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES);

        var attributesRequest= GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(atts)
                .build();

        GetQueueAttributesResponse response = sqsClient.getQueueAttributes(attributesRequest);

        var queueAtts = response.attributesAsStrings();
        for (Map.Entry<String,String> queueAtt : queueAtts.entrySet())
            logger.info("Key = " + queueAtt.getKey() +
                    ", Value = " + queueAtt.getValue());
    }
}
