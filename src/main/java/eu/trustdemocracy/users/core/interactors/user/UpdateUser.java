package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.UserInteractor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import java.util.UUID;
import lombok.val;

public class UpdateUser extends UserInteractor {

  public UpdateUser(UserDAO userDAO) {
    super(userDAO);
  }

  public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
    val inputUser = UserMapper.createEntity(userRequestDTO.getAccessToken());

    cleanUserState(inputUser.getId(), userRequestDTO);

    val user = UserMapper.createEntity(userRequestDTO);
    return UserMapper.createResponse(userDAO.update(user));
  }

  private void cleanUserState(UUID id, UserRequestDTO inputUser) {
    User user = userDAO.findById(id);
    if (user == null) {
      throw new IllegalStateException(
          "Trying to update unexisting user with id [" + id + "]");
    }

    fillEmptyAttributes(user, inputUser);

    inputUser.setUsername(user.getUsername());
  }

  private void fillEmptyAttributes(User user, UserRequestDTO userRequestDTO) {
    if (userRequestDTO.getEmail() == null || userRequestDTO.getEmail().isEmpty()) {
      userRequestDTO.setEmail(user.getEmail());
    }

    if (userRequestDTO.getName() == null) {
      userRequestDTO.setName(user.getName());
    }

    if (userRequestDTO.getSurname() == null) {
      userRequestDTO.setSurname(user.getSurname());
    }

    if (userRequestDTO.getVisibility() == null) {
      userRequestDTO.setVisibility(user.getVisibility());
    }
  }
}
