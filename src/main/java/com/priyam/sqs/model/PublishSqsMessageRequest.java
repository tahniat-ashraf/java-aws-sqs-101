package com.priyam.sqs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishSqsMessageRequest {

    @JsonProperty("message")
    private MyMessage message;
    @JsonProperty("messageGroupId")
    private String messageGroupId;
}
