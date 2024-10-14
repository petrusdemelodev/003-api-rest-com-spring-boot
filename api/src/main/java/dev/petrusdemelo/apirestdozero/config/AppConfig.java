package dev.petrusdemelo.apirestdozero.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class AppConfig {

  @Value("${aws.region:us-east-1}")
  private String awsRegion;

  @Bean
  @ConditionalOnProperty(name = "dynamodb.local", havingValue = "true")
  DynamoDbClient localDynamoDbClient(){
    return DynamoDbClient.builder()
            .region(Region.of(awsRegion))
            .endpointOverride(URI.create("http://localhost:8000"))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
  }

  @Bean
  @ConditionalOnMissingBean(DynamoDbClient.class)
  DynamoDbClient dynamodbClient(){
    return DynamoDbClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
  }  
}
