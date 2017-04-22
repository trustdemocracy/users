package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.utils.UserMapper;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public class UpdateUser {

  private UserDAO userDAO;

  public UpdateUser(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
    User user = UserMapper.createEntity(userRequestDTO);
    return UserMapper.createResponse(userDAO.update(user));
  }
}
