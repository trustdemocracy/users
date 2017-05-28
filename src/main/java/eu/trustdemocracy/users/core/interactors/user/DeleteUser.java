package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.out.MainGateway;
import eu.trustdemocracy.users.gateways.repositories.UserRepository;
import lombok.val;

public class DeleteUser  implements Interactor<UserRequestDTO, UserResponseDTO> {

  private UserRepository userRepository;
  private MainGateway mainGateway;

  public DeleteUser(UserRepository userRepository, MainGateway mainGateway) {
    this.userRepository = userRepository;
    this.mainGateway = mainGateway;
  }

  public UserResponseDTO execute(UserRequestDTO requestDTO) {
    val inputUser = UserMapper.createEntity(requestDTO.getAccessToken());

    val user = userRepository.deleteById(inputUser.getId());
    return UserMapper.createResponse(user);
  }
}
