package eu.trustdemocracy.users.gateways.repositories.mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoCollection;
import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
import eu.trustdemocracy.users.gateways.repositories.TokenRepository;
import java.util.UUID;
import lombok.val;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MongoTokenRepositoryTest {

  private MongoCollection<Document> collection;
  private TokenRepository tokenRepository;

  @BeforeEach
  public void init() {
    val fongo = new Fongo("test server");
    val db = fongo.getDatabase("test_database");
    collection = db.getCollection("tokens");
    tokenRepository = new MongoTokenRepository(db);
  }

  @Test
  public void storeRefreshToken() {
    val id = UUID.randomUUID();
    val token = CryptoUtils.randomToken();

    assertEquals(0L, collection.count());

    tokenRepository.storeRefreshToken(id, token);

    assertEquals(1L, collection.count());

    val condition = and(
        eq("id", id.toString()),
        eq("refreshToken", token)
    );

    val document = collection.find(condition).first();
    assertNotNull(document);

    assertEquals(id, UUID.fromString(document.getString("id")));
    assertEquals(token, document.getString("refreshToken"));
  }

  @Test
  public void findAndDeleteRefreshToken() {
    val id = UUID.randomUUID();
    val token = CryptoUtils.randomToken();

    assertEquals(0L, collection.count());
    tokenRepository.storeRefreshToken(id, token);
    assertEquals(1L, collection.count());

    assertTrue(tokenRepository.findAndDeleteRefreshToken(id, token));
    assertEquals(0L, collection.count());
  }


  @Test
  public void findAndDeleteInvalidRefreshToken() {
    val id = UUID.randomUUID();
    val token = CryptoUtils.randomToken();
    val invalidToken = CryptoUtils.randomToken();

    assertEquals(0L, collection.count());
    tokenRepository.storeRefreshToken(id, token);
    assertEquals(1L, collection.count());

    assertFalse(tokenRepository.findAndDeleteRefreshToken(id, invalidToken));
    assertEquals(1L, collection.count());
  }

}
