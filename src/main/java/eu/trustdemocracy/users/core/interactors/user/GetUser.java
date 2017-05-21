package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.UserInteractor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public class GetUser extends UserInteractor {

  public GetUser(UserDAO userDAO) {
    super(userDAO);
  }

  public UserResponseDTO execute(UserRequestDTO inputUser) {
    User user;
    if (inputUser.getId() == null) {
      user = userDAO.findByUsername(inputUser.getUsername());

    } else {
      user = userDAO.findById(inputUser.getId());
    }
    return UserMapper.createResponse(user);
  }
}
