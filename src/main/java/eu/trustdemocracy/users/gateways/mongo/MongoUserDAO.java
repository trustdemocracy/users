package eu.trustdemocracy.users.gateways.mongo;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.UserVisibility;
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
    val document = new Document("id", user.getId().toString())
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
  public User findByUsername(String username) {
    val userDocument = collection.find(eq("username", username)).first();
    return buildFromDocument(userDocument);
  }

  @Override
  public User update(User user) {
    val document = new Document("id", user.getId().toString())
        .append("username", user.getUsername())
        .append("email", user.getEmail())
        .append("password", user.getPassword())
        .append("name", user.getName() != null ? user.getName() : "")
        .append("surname", user.getSurname() != null ? user.getSurname() : "")
        .append("visibility", user.getVisibility().toString());
    collection.replaceOne(eq("id", user.getId().toString()), document);
    return user;
  }

  @Override
  public User findById(UUID id) {
    val userDocument = collection.find(eq("id", id.toString())).first();
    return buildFromDocument(userDocument);
  }

  @Override
  public void deleteById(UUID id) {
    collection.deleteOne(eq("id", id.toString()));
  }

  private User buildFromDocument(Document userDocument) {
    return new User()
        .setId(UUID.fromString((String) userDocument.get("id")))
        .setUsername(userDocument.getString("username"))
        .setEmail(userDocument.getString("email"))
        .setHashedPassword(userDocument.getString("password"))
        .setName(userDocument.getString("name"))
        .setSurname(userDocument.getString("surname"))
        .setVisibility(UserVisibility.valueOf(userDocument.getString("visibility")));
  }
}
