package eu.trustdemocracy.users.infrastructure;

import java.security.PrivateKey;

public final class JWTKeyFactory {

  private static PrivateKey privateKey;

  public static void setPrivateKey(PrivateKey privateKey) {
    JWTKeyFactory.privateKey = privateKey;
  }

  public static PrivateKey getPrivateKey() {
    return privateKey;
  }
}
