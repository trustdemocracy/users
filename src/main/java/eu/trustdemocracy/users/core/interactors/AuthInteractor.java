package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public abstract class AuthInteractor implements Interactor<UserRequestDTO, String> {

  protected UserDAO userDAO;

  public AuthInteractor(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

}
