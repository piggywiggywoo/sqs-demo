package org.cambridge.aop.sqsdemo;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SnsUsingJavaSDK {

    @Autowired
    AmazonSNS amazonSNSClient;

    @Value("${sns_topic_name}")
    private String snsTopicName;

    public PublishResult sendMessageToTopic(String message) {
        String topicArn = amazonSNSClient.createTopic(snsTopicName).getTopicArn();

        PublishRequest publishRequest = new PublishRequest(topicArn, message);
        publishRequest.addMessageAttributesEntry("addedBy", new MessageAttributeValue()
                .withStringValue("SNSDemoApp")
                .withDataType("String"));
        return amazonSNSClient.publish(publishRequest);
    }
}
