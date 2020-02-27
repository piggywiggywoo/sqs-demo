package org.cambridge.aop.sqsdemo;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SqsAndRestApiUsingCamel extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration()
        .component("servlet")
        .bindingMode(RestBindingMode.json);

        rest("/camel-demo")
        .post("/send-to-queue")
        .id("POST_/v1/camel-demo/send-to-queue")
        .description("Split array JSON request and send each part to SQS queue")
        .type(SampleJsonMessage.class)
        .route()
            .split(jsonpath("$.field2"))
                .marshal().json(JsonLibrary.Jackson)
                .to("aws-sqs://{{sqs_queue_name_camel}}?amazonSQSClient=#amazonSqsClient")
            .end();


        from("aws-sqs://{{sqs_queue_name_camel}}?amazonSQSClient=#amazonSqsClient")
        .routeId("SqsAndRestApiUsingCamel-consumeFromQueue")
        .description("Receive each part from SQS queue and do whatever")
        .log(LoggingLevel.INFO, log, "Received message from queue: ${body}");
    }
}
