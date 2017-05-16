package eu.trustdemocracy.users.core.interactors.exceptions;

public class CredentialsNotFoundException extends RuntimeException {

  public CredentialsNotFoundException(String message) {
    super(message);
  }

}
