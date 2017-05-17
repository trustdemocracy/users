package eu.trustdemocracy.users.core.interactors.exceptions;

public class InvalidTokenException extends RuntimeException {

  public InvalidTokenException(String message) {
    super(message);
  }

}
