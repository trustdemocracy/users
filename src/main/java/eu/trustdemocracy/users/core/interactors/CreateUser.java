package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.UserVisibility;
import eu.trustdemocracy.users.core.entities.utils.UserMapper;
import eu.trustdemocracy.users.core.interactors.exceptions.UsernameAlreadyExistsException;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public class CreateUser {

  private UserDAO userDAO;

  public CreateUser(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
    validateUserState(userRequestDTO);

    if (userDAO.findWithUsername(userRequestDTO.getUsername()) != null) {
      throw new UsernameAlreadyExistsException(
          "The username [" + userRequestDTO.getUsername() + "] already exists");
    }

    userRequestDTO.setVisibility(UserVisibility.PRIVATE);
    User user = userDAO.createUser(UserMapper.createEntity(userRequestDTO));
    return UserMapper.createResponse(user);
  }

  private void validateUserState(UserRequestDTO user) {
    if (user.getUsername().isEmpty()) {
      throw new IllegalStateException("The username cannot be empty");
    }

    if (user.getEmail().isEmpty()) {
      throw new IllegalStateException("The email cannot be empty");
    }

    if (user.getPassword().isEmpty()) {
      throw new IllegalStateException("The password cannot be empty");
    }
  }
}
