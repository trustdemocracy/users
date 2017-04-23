package eu.trustdemocracy.users.gateways.mongo;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.fakemongo.Fongo;
import com.mongodb.Block;
import com.mongodb.client.MongoDatabase;
import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.utils.CryptoUtils;
import eu.trustdemocracy.users.gateways.UserDAO;
import java.util.UUID;
import lombok.val;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MongoUserDAOTest {

  private MongoDatabase db;
  private UserDAO userDAO;

  @BeforeEach
  public void init() {
    val fongo = new Fongo("test server");
    db = fongo.getDatabase("test database");
    userDAO = new MongoUserDAO(db);
  }

  @Test
  public void createSingleUser() {
    val id = UUID.randomUUID();
    val user = new User()
        .setId(id)
        .setUsername("test")
        .setEmail("test@email.com")
        .setName("test@email.com")
        .setSurname("testSurname")
        .setPassword(CryptoUtils.hash("test"));

    assertEquals(user, userDAO.createUser(user));


    Block<Document> block = document -> assertEquals(user.getName(), document.toJson());

    db.getCollection("users")
        .find(eq("id", id))
        .forEach(block);
  }
}
