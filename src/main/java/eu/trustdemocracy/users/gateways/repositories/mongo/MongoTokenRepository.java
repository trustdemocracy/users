package eu.trustdemocracy.users.gateways.repositories.mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.trustdemocracy.users.gateways.repositories.TokenRepository;
import java.util.UUID;
import lombok.val;
import org.bson.Document;

public class MongoTokenRepository implements TokenRepository {

  private static final String TOKENS_COLLECTION = "tokens";
  private MongoCollection<Document> collection;

  public MongoTokenRepository(MongoDatabase db) {
    this.collection = db.getCollection(TOKENS_COLLECTION);
  }

  @Override
  public void storeRefreshToken(UUID userId, String refreshToken) {
    val document = new Document("id", userId.toString())
        .append("refreshToken", refreshToken);

    collection.insertOne(document);
  }

  @Override
  public boolean findAndDeleteRefreshToken(UUID userId, String refreshToken) {
    val condition = and(
        eq("id", userId.toString()),
        eq("refreshToken", refreshToken)
    );

    val deleteResult = collection.deleteMany(condition);

    return deleteResult.getDeletedCount() > 0;
  }
}
