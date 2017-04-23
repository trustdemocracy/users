package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.utils.UserMapper;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import lombok.val;

public class UpdateUser implements Interactor<UserRequestDTO, UserResponseDTO> {

  private UserDAO userDAO;

  public UpdateUser(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
    cleanUserState(userRequestDTO);

    val user = UserMapper.createEntity(userRequestDTO);
    return UserMapper.createResponse(userDAO.update(user));
  }

  private void cleanUserState(UserRequestDTO userRequestDTO) {
    User user = userDAO.findById(userRequestDTO.getId());
    if (user == null) {
      throw new IllegalStateException(
          "Trying to update unexisting user with id [" + userRequestDTO.getId() + "]");
    }

    userRequestDTO.setUsername(user.getUsername());
  }
}
