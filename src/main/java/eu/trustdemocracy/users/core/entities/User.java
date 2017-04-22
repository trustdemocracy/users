package eu.trustdemocracy.users.core.entities;

import eu.trustdemocracy.users.core.entities.utils.CryptoUtils;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
  private UUID id;
  private String username;
  private String email;
  private String password;
  private String name;
  private String surname;
  private UserVisibility visibility;

  public User setPassword(String password) {
    if (password != null) {
      password = CryptoUtils.hash(password);
    }
    this.password = password;
    return this;
  }
}
