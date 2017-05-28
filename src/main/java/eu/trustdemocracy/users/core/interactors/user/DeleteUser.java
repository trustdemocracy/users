package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserRepository;
import lombok.val;

public class DeleteUser  implements Interactor<UserRequestDTO, UserResponseDTO> {

  protected UserRepository userRepository;

  public DeleteUser(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserResponseDTO execute(UserRequestDTO requestDTO) {
    val inputUser = UserMapper.createEntity(requestDTO.getAccessToken());

    val user = userRepository.deleteById(inputUser.getId());
    return UserMapper.createResponse(user);
  }
}
