package eu.trustdemocracy.users.infrastructure;

import com.mongodb.MongoClient;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.mongo.MongoUserDAO;
import java.util.Properties;
import lombok.val;

public class DAOFactory {

  private static final String DATABASE_PROPERTIES_FILE = "database.properties";
  private static final String PROPERTIES_KEY_SERVER = "server";
  private static final String PROPERTIES_KEY_PORT = "port";
  private static final String PROPERTIES_KEY_DATABASE = "database";

  private static UserDAO userDAO;

  public static UserDAO getUserDAO() {
    if (userDAO == null) {
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
      val db = properties.getProperty(PROPERTIES_KEY_DATABASE);

      val mongoClient = new MongoClient(server, port);
      userDAO = new MongoUserDAO(mongoClient.getDatabase(db));
    }
    return userDAO;
  }


  private static void throwIfMissingKey(Properties properties, String key) {
    if (!properties.containsKey(key)) {
      throw new RuntimeException(
          "Unable to find key " + key + "in " + DATABASE_PROPERTIES_FILE);
    }
  }

  private static Properties getProperties() throws Exception {
    val properties = new Properties();
    val inputStream = DAOFactory.class.getClassLoader()
        .getResourceAsStream(DATABASE_PROPERTIES_FILE);
    properties.load(inputStream);
    inputStream.close();
    return properties;
  }
}
