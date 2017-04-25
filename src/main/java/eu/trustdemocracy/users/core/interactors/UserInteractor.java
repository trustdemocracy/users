package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public abstract class UserInteractor implements Interactor<UserRequestDTO,UserResponseDTO> {
  protected UserDAO userDAO;

  public UserInteractor(UserDAO userDAO) {
    this.userDAO = userDAO;
  }
}
