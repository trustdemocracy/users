package eu.trustdemocracy.users.core.interactors.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {

  public UsernameAlreadyExistsException(String message) {
    super(message);
  }
}
