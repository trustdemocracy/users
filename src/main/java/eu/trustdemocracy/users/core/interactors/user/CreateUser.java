package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.entities.UserVisibility;
import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.UserInteractor;
import eu.trustdemocracy.users.core.interactors.exceptions.UsernameAlreadyExistsException;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import lombok.val;

public class CreateUser extends UserInteractor {

  public CreateUser(UserDAO userDAO) {
    super(userDAO);
  }

  public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
    validateUserState(userRequestDTO);

    if (userDAO.findByUsername(userRequestDTO.getUsername()) != null) {
      throw new UsernameAlreadyExistsException(
          "The username [" + userRequestDTO.getUsername() + "] already exists");
    }

    userRequestDTO.setVisibility(UserVisibility.PRIVATE);
    val user = userDAO.create(UserMapper.createEntity(userRequestDTO));
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
