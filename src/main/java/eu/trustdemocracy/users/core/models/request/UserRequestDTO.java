package eu.trustdemocracy.users.core.models.request;

import eu.trustdemocracy.users.core.entities.UserVisibility;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserRequestDTO {

  private UUID id;
  private String username;
  private String email;
  private String password;
  private String name;
  private String surname;
  private UserVisibility visibility;
}
