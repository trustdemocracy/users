package eu.trustdemocracy.users.gateways.fake;

import eu.trustdemocracy.users.gateways.TokenDAO;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakeTokenDAO implements TokenDAO {

  private Map<UUID, String> refreshTokens = new HashMap<>();

  @Override
  public void storeRefreshToken(UUID userId, String refreshToken) {
    refreshTokens.put(userId, refreshToken);
  }

  @Override
  public boolean findRefreshToken(UUID userId, String refreshToken) {
    return refreshTokens.remove(userId, refreshToken);
  }
}
