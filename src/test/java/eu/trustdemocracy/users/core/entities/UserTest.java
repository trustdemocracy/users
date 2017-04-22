package eu.trustdemocracy.users.core.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.trustdemocracy.users.core.entities.utils.CryptoUtils;
import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  public void hashPasswordOnSet() {
    User user = new User();
    String password = "myTestPassword";

    assertEquals(null, user.getPassword());

    user.setPassword(password);

    assertNotEquals(password, user.getPassword());

    assertTrue(CryptoUtils.validate(user.getPassword(), password));
  }
}
