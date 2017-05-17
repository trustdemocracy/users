package eu.trustdemocracy.users.gateways.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.trustdemocracy.users.gateways.TokenDAO;
import java.util.UUID;
import lombok.val;
import org.bson.Document;

public class MongoTokenDAO implements TokenDAO {

  private static final String TOKENS_COLLECTION = "tokens";
  private MongoCollection<Document> collection;

  public MongoTokenDAO(MongoDatabase db) {
    this.collection = db.getCollection(TOKENS_COLLECTION);
  }

  @Override
  public void storeRefreshToken(UUID userId, String refreshToken) {
    val document = new Document("id", userId.toString())
        .append("refreshToken", refreshToken);

    collection.insertOne(document);
  }

  @Override
  public boolean findRefreshToken(UUID userId, String refreshToken) {
    return false;
  }
}
