package eu.trustdemocracy.users.gateways.fake;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.gateways.UserDAO;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.val;

public class FakeUserDAO implements UserDAO {
  private Map<UUID, User> users = new HashMap<>();
  private Map<UUID, String> refreshTokens = new HashMap<>();
  private UUID nextUniqueUUID;

  @Override
  public User create(User user) {
    UUID uuid = getUniqueUUID();
    user.setId(uuid);

    users.put(uuid, user);

    return user;
  }

  @Override
  public User findByUsername(String username) {
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
  public User deleteById(UUID id) {
    val user = users.get(id);
    users.remove(id);
    return user;
  }

  @Override
  public void storeRefreshToken(UUID userId, String refreshToken) {
    refreshTokens.put(userId, refreshToken);
  }

  @Override
  public boolean findRefreshToken(UUID userId, String refreshToken) {
    return refreshTokens.remove(userId, refreshToken);
  }

  public UUID getUniqueUUID() {
    while (nextUniqueUUID == null || users.containsKey(nextUniqueUUID)) {
      nextUniqueUUID = UUID.randomUUID();
    }

    return nextUniqueUUID;
  }
}
