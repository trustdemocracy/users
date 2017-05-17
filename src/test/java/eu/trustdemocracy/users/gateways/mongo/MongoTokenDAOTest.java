package eu.trustdemocracy.users.gateways.mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoCollection;
import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
import eu.trustdemocracy.users.gateways.TokenDAO;
import java.util.UUID;
import lombok.val;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MongoTokenDAOTest {

  private MongoCollection<Document> collection;
  private TokenDAO tokenDAO;

  @BeforeEach
  public void init() {
    val fongo = new Fongo("test server");
    val db = fongo.getDatabase("test_database");
    collection = db.getCollection("tokens");
    tokenDAO = new MongoTokenDAO(db);
  }

  @Test
  public void storeRefreshToken() {
    val id = UUID.randomUUID();
    val token = CryptoUtils.randomToken();

    assertEquals(0L, collection.count());

    tokenDAO.storeRefreshToken(id, token);

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

}
