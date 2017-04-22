package eu.trustdemocracy.users.gateways;

import eu.trustdemocracy.users.core.entities.User;
import java.util.UUID;

public interface UserDAO {

  User createUser(User user);

  UUID getUniqueUUID();

  User findWithUsername(String username);

  User update(User user);
}
