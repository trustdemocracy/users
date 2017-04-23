package eu.trustdemocracy.users.gateways.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.gateways.UserDAO;
import java.util.UUID;
import lombok.val;
import org.bson.Document;

public class MongoUserDAO implements UserDAO {

  private static final String USERS_COLLECTION = "users";
  private MongoCollection<Document> collection;

  public MongoUserDAO(MongoDatabase db) {
    this.collection = db.getCollection(USERS_COLLECTION);
  }

  @Override
  public User create(User user) {
    val document = new Document("id", user.getId())
        .append("username", user.getUsername())
        .append("email", user.getEmail())
        .append("password", user.getPassword())
        .append("name", user.getName() != null ? user.getName() : "")
        .append("surname", user.getSurname() != null ? user.getSurname() : "")
        .append("visibility", user.getVisibility().toString());
    collection.insertOne(document);
    return user;
  }

  @Override
  public UUID getUniqueUUID() {
    return null;
  }

  @Override
  public User findWithUsername(String username) {
    return null;
  }

  @Override
  public User update(User user) {
    return null;
  }

  @Override
  public User findById(UUID id) {
    return null;
  }

  @Override
  public void deleteById(UUID id) {

  }
}
