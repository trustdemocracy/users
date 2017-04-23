package eu.trustdemocracy.users.gateways.mongo;

import com.mongodb.client.MongoDatabase;
import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.gateways.UserDAO;
import java.util.UUID;

public class MongoUserDAO implements UserDAO {
  private MongoDatabase db;

  public MongoUserDAO(MongoDatabase db) {
    this.db = db;
  }

  @Override
  public User createUser(User user) {
    return null;
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
