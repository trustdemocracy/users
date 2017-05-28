package eu.trustdemocracy.users.gateways.mongo;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.fakemongo.Fongo;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.UserVisibility;
import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
import eu.trustdemocracy.users.gateways.UserRepository;
import java.util.UUID;
import lombok.val;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MongoUserRepositoryTest {

  private MongoCollection<Document> collection;
  private UserRepository userRepository;

  @BeforeEach
  public void init() {
    val fongo = new Fongo("test server");
    val db = fongo.getDatabase("test_database");
    collection = db.getCollection("users");
    userRepository = new MongoUserRepository(db);
  }

  @Test
  public void createSingleUser() {
    val user = new User()
        .setUsername("test")
        .setEmail("test@email.com")
        .setName("testName")
        .setSurname("testSurname")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    assertEquals(0L, collection.count());

    createUserAndAssignId(user);

    collection.find(eq("id", user.getId().toString()))
        .forEach(assertEqualsBlock(user));
  }

  @Test
  public void createBasicUser() {
    val user = new User()
        .setUsername("test")
        .setEmail("test@email.com")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    createUserAndAssignId(user);

    Block<Document> block = document -> {
      assertEquals(user.getUsername(), document.getString("username"));
      assertEquals(user.getEmail(), document.getString("email"));
      assertEquals(user.getPassword(), document.getString("password"));
      assertEquals(user.getVisibility().toString(), document.getString("visibility"));
      assertEquals("", document.getString("name"));
      assertEquals("", document.getString("surname"));
    };

    collection.find(eq("id", user.getId().toString()))
        .forEach(block);
  }

  @Test
  public void updateUser() {
    val user = new User()
        .setUsername("test")
        .setEmail("test@email.com")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    createUserAndAssignId(user);

    user.setEmail("newEmail@email.com")
        .setVisibility(UserVisibility.PUBLIC)
        .setPassword(CryptoUtils.hash("newPassword"))
        .setName("testName")
        .setSurname("testSurname");
    assertEquals(user, userRepository.update(user));

    collection.find(eq("id", user.getId().toString()))
        .forEach(assertEqualsBlock(user));
  }

  @Test
  public void deleteUser() {
    val user = new User()
        .setUsername("test")
        .setEmail("test@email.com")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    createUserAndAssignId(user);

    val deletedUser = userRepository.deleteById(user.getId());
    assertEquals(user, deletedUser);
    assertEquals(0L, collection.count(eq("id", user.getId().toString())));
  }

  @Test
  public void findById() {
    val user = new User()
        .setUsername("test")
        .setEmail("test@email.com")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    createUserAndAssignId(user);

    val userFound = userRepository.findById(user.getId());

    collection.find(eq("id", user.getId().toString()))
        .forEach(assertEqualsBlock(userFound));
  }

  @Test
  public void findByUsername() {
    val username = "test";
    val user = new User()
        .setUsername(username)
        .setEmail("test@email.com")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    createUserAndAssignId(user);

    val userFound = userRepository.findByUsername(username);
    assertNotNull(userFound);

    collection.find(eq("username", username))
        .forEach(assertEqualsBlock(userFound));

  }

  @Test
  public void findAll() {
    for (int i = 0; i < 30; i++) {
      val user = new User()
          .setUsername("test" + i)
          .setEmail("test@email.com")
          .setVisibility(UserVisibility.PRIVATE)
          .setPassword(CryptoUtils.hash("test" + i));

      createUserAndAssignId(user);
    }

    assertEquals(30, userRepository.findAll().size());
  }

  private User createUserAndAssignId(User user) {
    val createdUser = userRepository.create(user);
    user.setId(createdUser.getId());
    assertEquals(user, createdUser);
    assertNotEquals(0L, collection.count(eq("id", createdUser.getId().toString())));
    return createdUser;
  }

  private Block<Document> assertEqualsBlock(User user) {
    return document -> {
      val name = document.getString("name");
      val surname = document.getString("surname");

      assertEquals(user.getId(), UUID.fromString((String) document.get("id")));
      assertEquals(user.getUsername(), document.getString("username"));
      assertEquals(user.getEmail(), document.getString("email"));
      assertEquals(user.getPassword(), document.getString("password"));
      assertEquals(user.getVisibility().toString(), document.getString("visibility"));
      assertEquals(user.getName(), name.isEmpty() ? null : name);
      assertEquals(user.getSurname(), surname.isEmpty() ? null : surname);
    };
  }
}
