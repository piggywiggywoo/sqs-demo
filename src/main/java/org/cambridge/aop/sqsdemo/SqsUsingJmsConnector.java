package org.cambridge.aop.sqsdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SqsUsingJmsConnector  {

    @JmsListener(destination = "${sqs_queue_name_async}")
    public void receiveMessageAsync(@Payload final Message<String> message) {
        log.info("Received async message from queue: [{}]", message.getPayload());
    }
}
