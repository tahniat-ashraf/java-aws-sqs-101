package com.priyam.sqs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveMessageResponse {

    private String messageId;
    private MyMessage message;
    private String receiptHandle;
}
