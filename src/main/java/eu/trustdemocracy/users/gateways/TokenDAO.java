package eu.trustdemocracy.users.gateways;

import java.util.UUID;

public interface TokenDAO {

  void storeRefreshToken(UUID userId, String refreshToken);

  boolean findAndDeleteRefreshToken(UUID userId, String refreshToken);
}
