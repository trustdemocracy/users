package eu.trustdemocracy.users.gateways.fake;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.gateways.UserDAO;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakeUserDAO implements UserDAO {
  private Map<UUID, User> users = new HashMap<>();
  private UUID nextUniqueUUID;

  @Override
  public User createUser(User user) {
    UUID uuid = getUniqueUUID();
    user.setId(uuid);

    users.put(uuid, user);

    return user;
  }

  @Override
  public UUID getUniqueUUID() {
    while (nextUniqueUUID == null || users.containsKey(nextUniqueUUID)) {
      nextUniqueUUID = UUID.randomUUID();
    }

    return nextUniqueUUID;
  }

  @Override
  public User findWithUsername(String username) {
    return users.values().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst()
        .orElse(null);
  }

  @Override
  public User update(User user) {
    users.replace(user.getId(), user);
    return user;
  }

  @Override
  public User findById(UUID id) {
    return users.get(id);
  }

  @Override
  public void deleteById(UUID id) {
    users.remove(id);
  }
}
