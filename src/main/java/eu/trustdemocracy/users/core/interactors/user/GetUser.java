package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.interactors.exceptions.UserNotFoundException;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.repositories.UserRepository;

public class GetUser  implements Interactor<UserRequestDTO, UserResponseDTO> {

  private UserRepository userRepository;

  public GetUser(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserResponseDTO execute(UserRequestDTO inputUser) {
    User user;
    if (inputUser.getId() == null) {
      user = userRepository.findByUsername(inputUser.getUsername());

    } else {
      user = userRepository.findById(inputUser.getId());
    }

    if (user == null) {
      throw new UserNotFoundException("The user with id [" + inputUser.getId() + "] "
          + "or username [" + inputUser.getUsername() + "] doesn't exist.");
    }

    return UserMapper.createResponse(user);
  }
}
