package org.cambridge.aop.sqsdemo;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SqsUsingJavaSDK {

    @Autowired
    AmazonSQS amazonSqsClient;

    @Value("${sqs-queue-name}")
    private String sqsQueueName;

    public SendMessageResult sendMessage(String queueName, String message) {
        String queueUrl = amazonSqsClient.createQueue(StringUtils.isEmpty(queueName) ? sqsQueueName : queueName).getQueueUrl();
        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, message);
        sendMessageRequest.addMessageAttributesEntry("addedBy", new MessageAttributeValue()
               .withStringValue("SQSDemoApp")
               .withDataType("String"));
        return amazonSqsClient.sendMessage(sendMessageRequest);
    }

    public List<Message> receiveMessage() {
        String queueUrl = amazonSqsClient.createQueue(sqsQueueName).getQueueUrl();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
            .withWaitTimeSeconds(5)
            .withMaxNumberOfMessages(5)
            .withMessageAttributeNames("addedBy");
        ReceiveMessageResult receiveMessageResult = amazonSqsClient.receiveMessage(receiveMessageRequest);
        return receiveMessageResult.getMessages();
    }

    public List<Message> receiveMessageAndRemoveFromQueue() {
        List<Message> messages = receiveMessage();

        if (!CollectionUtils.isEmpty(messages)) {
            String queueUrl = amazonSqsClient.createQueue(sqsQueueName).getQueueUrl();
            List<DeleteMessageBatchRequestEntry> deleteMessageBatchRequestEntries = messages.stream()
                    .map(m -> new DeleteMessageBatchRequestEntry().withId(m.getMessageId()).withReceiptHandle(m.getReceiptHandle()))
                    .collect(Collectors.toList());
            DeleteMessageBatchRequest deleteMessageBatchRequest = new DeleteMessageBatchRequest()
                    .withQueueUrl(queueUrl)
                    .withEntries(deleteMessageBatchRequestEntries);
            amazonSqsClient.deleteMessageBatch(deleteMessageBatchRequest);
        }

        return messages;
    }

}
