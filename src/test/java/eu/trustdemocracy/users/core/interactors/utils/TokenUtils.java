package eu.trustdemocracy.users.core.interactors.utils;

import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import lombok.val;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;

public final class TokenUtils {

  public static void generateKeys() {
    try {
      val rsaKey = RsaJwkGenerator.generateJwk(2048);
      JWTKeyFactory.setPrivateKey(rsaKey.getPrivateKey());
      JWTKeyFactory.setPublicKey(rsaKey.getPublicKey());
    } catch (JoseException e) {
      throw new RuntimeException(e);
    }
  }

}
