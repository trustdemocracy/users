package eu.trustdemocracy.users.core.interactors.auth;

import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.gateways.UserDAO;

public class RefreshToken implements Interactor<String, String> {

  private UserDAO userDAO;

  public RefreshToken(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public String execute(String token) {
    return null;
  }
}
