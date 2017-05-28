package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.GetUsersResponseDTO;
import eu.trustdemocracy.users.gateways.repositories.UserRepository;
import java.util.List;

public class GetUsers implements Interactor<UserRequestDTO, GetUsersResponseDTO> {

  private UserRepository userRepository;

  public GetUsers(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public GetUsersResponseDTO execute(UserRequestDTO requestDTO) {
    User user = UserMapper.createEntity(requestDTO.getAccessToken());

    List<User> users = userRepository.findAll();

    return UserMapper.createResponse(users);
  }
}
