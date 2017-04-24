package eu.trustdemocracy.users.core.entities.util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public final class CryptoUtils {

  public static String hash(String password) {
    Argon2 argon2 = Argon2Factory.create();

    char[] result = password.toCharArray();

    String hash = argon2.hash(2, 65536, 1, result);
    argon2.wipeArray(result);

    return hash;
  }

  public static boolean validate(String hash, String password) {
    Argon2 argon2 = Argon2Factory.create();
    char[] charPassword = password.toCharArray();

    boolean areEqual = argon2.verify(hash, charPassword);
    argon2.wipeArray(charPassword);

    return areEqual;
  }
}
