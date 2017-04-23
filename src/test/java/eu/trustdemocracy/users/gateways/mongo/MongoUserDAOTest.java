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
import eu.trustdemocracy.users.core.entities.utils.CryptoUtils;
import eu.trustdemocracy.users.gateways.UserDAO;
import java.util.UUID;
import lombok.val;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MongoUserDAOTest {

  private MongoCollection<Document> collection;
  private UserDAO userDAO;

  @BeforeEach
  public void init() {
    val fongo = new Fongo("test server");
    val db = fongo.getDatabase("test_database");
    collection = db.getCollection("users");
    userDAO = new MongoUserDAO(db);
  }

  @Test
  public void createSingleUser() {
    val id = UUID.randomUUID();
    val user = new User()
        .setId(id)
        .setUsername("test")
        .setEmail("test@email.com")
        .setName("testName")
        .setSurname("testSurname")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    assertEquals(0L, collection.count(eq("id", id.toString())));
    assertEquals(user, userDAO.create(user));
    assertNotEquals(0L, collection.count(eq("id", id.toString())));

    collection.find(eq("id", id.toString()))
        .forEach(assertEqualsBlock(user));
  }

  @Test
  public void createBasicUser() {
    val id = UUID.randomUUID();
    val user = new User()
        .setId(id)
        .setUsername("test")
        .setEmail("test@email.com")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    assertEquals(user, userDAO.create(user));
    assertNotEquals(0L, collection.count(eq("id", id.toString())));

    Block<Document> block = document -> {
      assertEquals(user.getUsername(), document.getString("username"));
      assertEquals(user.getEmail(), document.getString("email"));
      assertEquals(user.getPassword(), document.getString("password"));
      assertEquals(user.getVisibility().toString(), document.getString("visibility"));
      assertEquals("", document.getString("name"));
      assertEquals("", document.getString("surname"));
    };

    collection.find(eq("id", id.toString()))
        .forEach(block);
  }

  @Test
  public void updateUser() {
    val id = UUID.randomUUID();
    val user = new User()
        .setId(id)
        .setUsername("test")
        .setEmail("test@email.com")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    userDAO.create(user);
    assertNotEquals(0L, collection.count(eq("id", id.toString())));

    user.setEmail("newEmail@email.com")
        .setVisibility(UserVisibility.PUBLIC)
        .setPassword(CryptoUtils.hash("newPassword"))
        .setName("testName")
        .setSurname("testSurname");
    assertEquals(user, userDAO.update(user));

    collection.find(eq("id", id.toString()))
        .forEach(assertEqualsBlock(user));
  }

  @Test
  public void deleteUser() {
    val id = UUID.randomUUID();
    val user = new User()
        .setId(id)
        .setUsername("test")
        .setEmail("test@email.com")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    userDAO.create(user);
    assertNotEquals(0L, collection.count(eq("id", id.toString())));

    userDAO.deleteById(id);
    assertEquals(0L, collection.count(eq("id", id.toString())));
  }

  @Test
  public void findById() {
    val id = UUID.randomUUID();
    val user = new User()
        .setId(id)
        .setUsername("test")
        .setEmail("test@email.com")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    userDAO.create(user);
    assertNotEquals(0L, collection.count(eq("id", id.toString())));
    val userFound = userDAO.findById(id);

    collection.find(eq("id", id.toString()))
        .forEach(assertEqualsBlock(userFound));
  }

  @Test
  public void findByUsername() {
    val id = UUID.randomUUID();
    val username = "test";
    val user = new User()
        .setId(id)
        .setUsername(username)
        .setEmail("test@email.com")
        .setVisibility(UserVisibility.PRIVATE)
        .setPassword(CryptoUtils.hash("test"));

    userDAO.create(user);
    assertNotEquals(0L, collection.count(eq("username", username)));
    val userFound = userDAO.findByUsername(username);
    assertNotNull(userFound);

    collection.find(eq("username", username))
        .forEach(assertEqualsBlock(userFound));

  }

  private Block<Document> assertEqualsBlock(User user) {
    return document -> {
      assertEquals(user.getId(), UUID.fromString((String) document.get("id")));
      assertEquals(user.getUsername(), document.getString("username"));
      assertEquals(user.getEmail(), document.getString("email"));
      assertEquals(user.getPassword(), document.getString("password"));
      assertEquals(user.getVisibility().toString(), document.getString("visibility"));
      assertEquals(user.getName(), document.getString("name"));
      assertEquals(user.getSurname(), document.getString("surname"));
    };
  }
}
