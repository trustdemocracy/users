package eu.trustdemocracy.users.gateways.mongo;

import com.mongodb.client.MongoDatabase;
import eu.trustdemocracy.users.gateways.TokenDAO;
import java.util.UUID;

public class MongoTokenDAO implements TokenDAO {

  public MongoTokenDAO(MongoDatabase database) {

  }

  @Override
  public void storeRefreshToken(UUID userId, String refreshToken) {

  }

  @Override
  public boolean findRefreshToken(UUID userId, String refreshToken) {
    return false;
  }
}
