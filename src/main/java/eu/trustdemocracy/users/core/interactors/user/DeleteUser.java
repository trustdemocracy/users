package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.UserInteractor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import lombok.val;

public class DeleteUser extends UserInteractor {

  public DeleteUser(UserDAO userDAO) {
    super(userDAO);
  }

  public UserResponseDTO execute(UserRequestDTO requestDTO) {
    val inputUser = UserMapper.createEntity(requestDTO.getAccessToken());

    val user = userDAO.deleteById(inputUser.getId());
    return UserMapper.createResponse(user);
  }
}
