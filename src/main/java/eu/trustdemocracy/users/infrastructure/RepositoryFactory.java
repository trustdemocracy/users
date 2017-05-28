package eu.trustdemocracy.users.infrastructure;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import eu.trustdemocracy.users.gateways.repositories.TokenRepository;
import eu.trustdemocracy.users.gateways.repositories.UserRepository;
import eu.trustdemocracy.users.gateways.repositories.mongo.MongoTokenRepository;
import eu.trustdemocracy.users.gateways.repositories.mongo.MongoUserRepository;
import java.util.Properties;
import lombok.val;

public class RepositoryFactory {

  private static final String DATABASE_PROPERTIES_FILE = "database.properties";
  private static final String PROPERTIES_KEY_SERVER = "server";
  private static final String PROPERTIES_KEY_PORT = "port";
  private static final String PROPERTIES_KEY_DATABASE = "database";

  private static MongoDatabase db;

  public static UserRepository getUserDAO() {
    return new MongoUserRepository(getDatabase());
  }

  public static TokenRepository getTokenDAO() {
    return new MongoTokenRepository(getDatabase());
  }

  private static MongoDatabase getDatabase() {
    if (db == null) {
      Properties properties;
      try {
        properties = getProperties();
      } catch (Exception e) {
        throw new RuntimeException("Failed to read database properties file ["
            + DATABASE_PROPERTIES_FILE + "]", e);
      }

      throwIfMissingKey(properties, PROPERTIES_KEY_SERVER);
      throwIfMissingKey(properties, PROPERTIES_KEY_PORT);
      throwIfMissingKey(properties, PROPERTIES_KEY_DATABASE);

      val server = properties.getProperty(PROPERTIES_KEY_SERVER);
      val port = Integer.valueOf(properties.getProperty(PROPERTIES_KEY_PORT));
      val dbKey = properties.getProperty(PROPERTIES_KEY_DATABASE);

      val mongoClient = new MongoClient(server, port);
      db = mongoClient.getDatabase(dbKey);
    }
    return db;
  }


  private static void throwIfMissingKey(Properties properties, String key) {
    if (!properties.containsKey(key)) {
      throw new RuntimeException(
          "Unable to find key " + key + "in " + DATABASE_PROPERTIES_FILE);
    }
  }

  private static Properties getProperties() throws Exception {
    val properties = new Properties();
    val inputStream = RepositoryFactory.class.getClassLoader()
        .getResourceAsStream(DATABASE_PROPERTIES_FILE);
    properties.load(inputStream);
    inputStream.close();

    loadSystemProperties(properties);

    return properties;
  }

  private static void loadSystemProperties(Properties properties) {
    val dbHost = System.getenv("db_host");
    if (dbHost != null) {
      properties.put(PROPERTIES_KEY_SERVER, dbHost);
    }
  }
}
