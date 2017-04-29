package eu.trustdemocracy.users.gateways.mongo;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.UserVisibility;
import eu.trustdemocracy.users.gateways.UserDAO;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.UUID;
import lombok.val;
import org.bson.Document;

public class MongoUserDAO implements UserDAO {
  private static final Logger LOG = LoggerFactory.getLogger(MongoUserDAO.class);

  private static final String USERS_COLLECTION = "users";
  private MongoCollection<Document> collection;

  public MongoUserDAO(MongoDatabase db) {
    this.collection = db.getCollection(USERS_COLLECTION);
  }

  @Override
  public User create(User user) {
    user.setId(getUniqueUUID());

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
    if (userDocument == null) {
      return null;
    }

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
    if (userDocument == null) {
      return null;
    }

    return buildFromDocument(userDocument);
  }

  @Override
  public User deleteById(UUID id) {
    val userDocument = collection.find(eq("id", id.toString())).first();
    if (userDocument == null) {
      return null;
    }

    collection.deleteOne(userDocument);
    return buildFromDocument(userDocument);
  }

  private UUID getUniqueUUID() {
    UUID id;
    do {
      id = UUID.randomUUID();
    } while (findById(id) != null);
    return id;
  }

  private User buildFromDocument(Document userDocument) {
    val name = userDocument.getString("name");
    val surname = userDocument.getString("surname");

    return new User()
        .setId(UUID.fromString((String) userDocument.get("id")))
        .setUsername(userDocument.getString("username"))
        .setEmail(userDocument.getString("email"))
        .setHashedPassword(userDocument.getString("password"))
        .setName(name.isEmpty() ? null : name)
        .setSurname(surname.isEmpty() ? null : surname)
        .setVisibility(UserVisibility.valueOf(userDocument.getString("visibility")));
  }
}