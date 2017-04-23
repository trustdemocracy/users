package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public class DeleteUser {
  private UserDAO userDAO;

  public DeleteUser(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  public void execute(UserRequestDTO inputUser) {
    userDAO.deleteById(inputUser.getId());
  }
}
