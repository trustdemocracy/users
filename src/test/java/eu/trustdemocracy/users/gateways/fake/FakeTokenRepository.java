package eu.trustdemocracy.users.gateways.fake;

import eu.trustdemocracy.users.gateways.TokenRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakeTokenRepository implements TokenRepository {

  private Map<UUID, String> refreshTokens = new HashMap<>();

  @Override
  public void storeRefreshToken(UUID userId, String refreshToken) {
    refreshTokens.put(userId, refreshToken);
  }

  @Override
  public boolean findAndDeleteRefreshToken(UUID userId, String refreshToken) {
    return refreshTokens.remove(userId, refreshToken);
  }
}
