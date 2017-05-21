package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.GetUsersResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import java.util.List;

public class GetUsers implements Interactor<UserRequestDTO, GetUsersResponseDTO> {

  private UserDAO userDAO;

  public GetUsers(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public GetUsersResponseDTO execute(UserRequestDTO requestDTO) {
    User user = UserMapper.createEntity(requestDTO.getAccessToken());

    List<User> users = userDAO.findAll();

    return UserMapper.createResponse(users);
  }
}
