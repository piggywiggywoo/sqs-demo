package org.cambridge.aop.sqsdemo;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;

@Slf4j
@EnableJms
@Configuration
@SpringBootApplication
public class Application {

    @Value("${aws.configurations.sqs-endpoint}")
    private String awsSqsConfigEndpoint;
    @Value("${aws.configurations.region}")
    private String awsSqsConfigRegion;

    // Begin AWS config

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        DefaultAWSCredentialsProviderChain defaultAWSCredentialsProviderChain =
                DefaultAWSCredentialsProviderChain.getInstance();
        defaultAWSCredentialsProviderChain.getCredentials();
        return defaultAWSCredentialsProviderChain;
    }

    @Bean
    public AmazonSQS amazonSqsClient() {
        return AmazonSQSClientBuilder.standard().withCredentials(this.awsCredentialsProvider())
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(this.awsSqsConfigEndpoint,
                        Regions.fromName(this.awsSqsConfigRegion).getName()))
                .build();
    }

    @Bean
    public AmazonSNS amazonSnsClient() {
        return AmazonSNSClientBuilder.standard().withCredentials(this.awsCredentialsProvider())
                .withRegion(Regions.fromName(this.awsSqsConfigRegion).getName())
                .build();
    }

    // End AWS config


    // Begin JMS dependencies

    @Bean
    public SQSConnectionFactory connectionFactory() {
        return new SQSConnectionFactory(new ProviderConfiguration().withNumberOfMessagesToPrefetch(5), amazonSqsClient());
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory =
        new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("3-10");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public JmsTemplate defaultJmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }

    // End JMS dependencies

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
