package dev.petrusdemelo.apirestdozero.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import dev.petrusdemelo.apirestdozero.domain.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@TestInstance(Lifecycle.PER_CLASS)
class DynamodbUserRepositoryTest {
  public DynamodbUserRepository dynamoDbUserRepository;
  @Mock DynamoDbClient dynamoDbClient;

  @BeforeAll
  void setup() {
    MockitoAnnotations.openMocks(this);
    this.dynamoDbUserRepository = new DynamodbUserRepository(dynamoDbClient);
    ReflectionTestUtils.setField(this.dynamoDbUserRepository, "tableName", "dev-users");
  }

  @BeforeEach
  void resetMocks() {
    reset(this.dynamoDbClient);
  }

  @Test
  void itShouldCreateAnUser() {
    // given
    var user = User.builder()
              .name("petrusdemelo")
              .email("petrusdemelo@gmail.com")
              .password("123456")
              .build();

    // when
    this.dynamoDbUserRepository.createUser(user);

    // then
    var capture = ArgumentCaptor.forClass(PutItemRequest.class);
    verify(this.dynamoDbClient, times(1)).putItem(capture.capture());

    var request = capture.getValue();    
    assertEquals("dev-users", request.tableName());
    assertEquals("petrusdemelo", request.item().get("name").s());
    assertEquals("petrusdemelo@gmail.com", request.item().get("email").s());
    assertEquals("123456", request.item().get("password").s());
    assertNotNull(request.item().get("created_at").s());
    assertNotNull(request.item().get("updated_at").s());
  }

  @Test
  void itShouldSaveAnUser() {
    // given
    var user = User.builder()
              .name("petrusdemelo")
              .email("petrusdemelo@gmail.com")
              .password("123456")
              .build();

    // when
    this.dynamoDbUserRepository.save(user);

    // then
    var capture = ArgumentCaptor.forClass(PutItemRequest.class);
    verify(this.dynamoDbClient, times(1)).putItem(capture.capture());

    var request = capture.getValue();    
    assertEquals("dev-users", request.tableName());
    assertEquals("petrusdemelo", request.item().get("name").s());
    assertEquals("petrusdemelo@gmail.com", request.item().get("email").s());
    assertEquals("123456", request.item().get("password").s());
    assertNotNull(request.item().get("created_at").s());
    assertNotNull(request.item().get("updated_at").s());
  }

  @Test
  void testDeleteUser() {
    // given
    var userID = UUID.randomUUID();

    // when
    this.dynamoDbUserRepository.deleteUserByID(userID);

    // then
    var capture = ArgumentCaptor.forClass(DeleteItemRequest.class);
    verify(this.dynamoDbClient, times(1)).deleteItem(capture.capture());
    var request = capture.getValue();
    assertEquals("dev-users", request.tableName());
    assertEquals(userID.toString(), request.key().get("id").s());
  }

  @Test
  void itShouldReturnAnUserByEmailIfUserExistsOnDynamoDB() {
    // given
    var email = "john.doe@gmail.com";

    var map = Map.of(
      "id", AttributeValue.builder().s(UUID.randomUUID().toString()).build(),
      "name", AttributeValue.builder().s("Petrus de Melo").build(),
      "email", AttributeValue.builder().s("petrusdemelo@gmail.com").build(),
      "password", AttributeValue.builder().s("123456").build(),
      "created_at", AttributeValue.builder().s(String.valueOf(LocalDateTime.now())).build(),
      "updated_at", AttributeValue.builder().s(String.valueOf(LocalDateTime.now())).build()
    );

    var response = QueryResponse.builder()
                    .count(1)
                    .items(List.of(map))
                    .build();

    when(this.dynamoDbClient.query(any(QueryRequest.class))).thenReturn(response);

    // when
    var result = this.dynamoDbUserRepository.findByEmail(email);

    // then
    assertTrue(result.isPresent());
    verify(this.dynamoDbClient, times(1)).query(any(QueryRequest.class));
    var user = result.get();
    assertEquals("Petrus de Melo", user.getName());
    assertEquals("petrusdemelo@gmail.com", user.getEmail());
    assertEquals("123456", user.getPassword());
    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());
  }

  @Test
  void itShouldReturnEmptyIfUserDoesntExistByEmail() {
    // given
    var email = "john.doe@gmail.com";

    var response = QueryResponse.builder()
                    .count(0)
                    .items(Collections.emptyList())
                    .build();

    when(this.dynamoDbClient.query(any(QueryRequest.class))).thenReturn(response);

    // when
    var result = this.dynamoDbUserRepository.findByEmail("john.doe@gmail.com");

    // then
    assertFalse(result.isPresent());
    verify(this.dynamoDbClient, times(1)).query(any(QueryRequest.class));
  }

  @Test
  void itShouldReturnAnUserIfUserExistByID() {
    // given
    var userID = UUID.randomUUID();
    var map = Map.of(
      "id", AttributeValue.builder().s(UUID.randomUUID().toString()).build(),
      "name", AttributeValue.builder().s("Petrus de Melo").build(),
      "email", AttributeValue.builder().s("petrusdemelo@gmail.com").build(),
      "password", AttributeValue.builder().s("123456").build(),
      "created_at", AttributeValue.builder().s(String.valueOf(LocalDateTime.now())).build(),
      "updated_at", AttributeValue.builder().s(String.valueOf(LocalDateTime.now())).build()
    );

    var response = GetItemResponse.builder().item(map).build();
    when(this.dynamoDbClient.getItem(any(GetItemRequest.class)))
      .thenReturn(response);

    // when
    var optionalUser = this.dynamoDbUserRepository.findById(userID);

    // then
    assertTrue(optionalUser.isPresent());
    var user = optionalUser.get();
    assertEquals("Petrus de Melo", user.getName());
    assertEquals("petrusdemelo@gmail.com", user.getEmail());
    assertEquals("123456", user.getPassword());
    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());

    verify(this.dynamoDbClient, times(1))
      .getItem(any(GetItemRequest.class));
  }

  @Test
  void itShouldReturnEmptyIfUserDoesntExistByID() {
    // given
    var userID = UUID.randomUUID();

    var response = GetItemResponse.builder().item(null).build();

    when(this.dynamoDbClient.getItem(any(GetItemRequest.class)))
      .thenReturn(response);

    // when
    var result = this.dynamoDbUserRepository.findById(userID);

    // then
    assertFalse(result.isPresent());
    verify(this.dynamoDbClient, times(1))
      .getItem(any(GetItemRequest.class));
  }
}
