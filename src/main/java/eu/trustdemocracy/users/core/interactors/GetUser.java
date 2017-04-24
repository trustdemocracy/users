package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import lombok.val;

public class GetUser implements Interactor<UserRequestDTO, UserResponseDTO> {
  private UserDAO userDAO;

  public GetUser(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  public UserResponseDTO execute(UserRequestDTO inputUser) {
    val user = userDAO.findById(inputUser.getId());
    return UserMapper.createResponse(user);
  }
}
