package org.cambridge.aop.sqsdemo;

import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/sqs-demo")
public class ApiController {

    @Autowired
    SqsUsingJavaSDK sqsUsingJavaSDK;
    @Autowired
    SnsUsingJavaSDK snsUsingJavaSDK;

    @RequestMapping(method = RequestMethod.POST, value = "/send-to-queue/{queueName}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<SendMessageResult> sendToQueue(@PathVariable("queueName") String queueName, @RequestBody Map<String,Object> requestBody) throws Exception {
        //Insert whatever application logic here
        String messageBody = new ObjectMapper().writeValueAsString(requestBody);
        return ResponseEntity.ok(sqsUsingJavaSDK.sendMessage(queueName, messageBody));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/poll-from-queue", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<List<Message>> pollFromQueue() throws Exception {
        return ResponseEntity.ok(sqsUsingJavaSDK.receiveMessage());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/poll-from-queue-and-delete", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<List<Message>> pollFromQueueAndDelete() throws Exception {
        return ResponseEntity.ok(sqsUsingJavaSDK.receiveMessageAndRemoveFromQueue());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/send-to-topic", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<PublishResult> sendToTopic(@RequestBody Map<String,Object> requestBody) throws Exception {
        String messageBody = new ObjectMapper().writeValueAsString(requestBody);
        return ResponseEntity.ok(snsUsingJavaSDK.sendMessageToTopic(messageBody));
    }
}
