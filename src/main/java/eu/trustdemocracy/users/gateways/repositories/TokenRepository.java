package eu.trustdemocracy.users.gateways.repositories;

import java.util.UUID;

public interface TokenRepository {

  void storeRefreshToken(UUID userId, String refreshToken);

  boolean findAndDeleteRefreshToken(UUID userId, String refreshToken);
}
