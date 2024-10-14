package dev.petrusdemelo.apirestdozero.repository.impl;

import java.util.Optional;
import java.util.UUID;
import java.util.Map;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import dev.petrusdemelo.apirestdozero.domain.User;
import dev.petrusdemelo.apirestdozero.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

@Repository
@RequiredArgsConstructor
public class DynamodbUserRepository implements UserRepository{
  private final DynamoDbClient dynamodbClient;

  @Value("${dynamodb.table:dev-users}")
  private String tableName;  
  
  public Optional<User> findByEmail(String email) {
    var request = QueryRequest.builder()
                    .tableName(tableName)
                    .indexName("email_index")
                    .expressionAttributeValues(Map.of(":email", AttributeValue.builder().s(email).build()))
                    .keyConditionExpression("email = :email")
                  .build();

    var response = this.dynamodbClient.query(request);

    if(response.count().equals(0)){
      return Optional.empty();
    }

    var item = response.items().get(0);    
    return Optional.of(this.mapToUser(item));
  }

  public Optional<User> findById(UUID id) {
    var request = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of("id", AttributeValue.builder().s(id.toString()).build()))
                  .build();
    
    var response = this.dynamodbClient.getItem(request);

    if(!response.hasItem()){
      return Optional.empty();
    }

    var item = response.item();
    return Optional.of(this.mapToUser(item));
  }

  public User createUser(User user) {
    var record = Map.of(
      "id", AttributeValue.builder().s(user.getId().toString()).build(),
      "name", AttributeValue.builder().s(user.getName()).build(),
      "email", AttributeValue.builder().s(user.getEmail()).build(),
      "password", AttributeValue.builder().s(user.getPassword()).build(),
      "created_at", AttributeValue.builder().s(user.getCreatedAt().toString()).build(),
      "updated_at", AttributeValue.builder().s(user.getUpdatedAt().toString()).build()
    );

    var request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(record)
                    .conditionExpression("attribute_not_exists(email)")
                    .build();
    
    this.dynamodbClient.putItem(request);
    return user;
  }

  public void deleteUserByID(UUID id) {
    var request = DeleteItemRequest.builder()
                  .tableName(tableName)
                  .key(Map.of("id", AttributeValue.builder().s(id.toString()).build()))  
                  .build();
    
    this.dynamodbClient.deleteItem(request);
  }

  public User save(User user) {
    var record = Map.of(
      "id", AttributeValue.builder().s(user.getId().toString()).build(),
      "name", AttributeValue.builder().s(user.getName()).build(),
      "email", AttributeValue.builder().s(user.getEmail()).build(),
      "password", AttributeValue.builder().s(user.getPassword()).build(),
      "created_at", AttributeValue.builder().s(user.getCreatedAt().toString()).build(),
      "updated_at", AttributeValue.builder().s(user.getUpdatedAt().toString()).build()
    );

    var request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(record)
                    .build();
    
    this.dynamodbClient.putItem(request);
    return user;
  }  

  private User mapToUser(Map<String, AttributeValue> item){
    return User.builder()
            .id(UUID.fromString(item.get("id").s()))
            .email(item.get("email").s())
            .name(item.get("name").s())
            .password(item.get("password").s())
            .createdAt(LocalDateTime.parse(item.get("created_at").s()))
            .updatedAt(LocalDateTime.parse(item.get("updated_at").s()))
          .build();
  }
}
