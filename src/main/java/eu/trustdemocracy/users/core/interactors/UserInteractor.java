package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserRepository;

public abstract class UserInteractor implements Interactor<UserRequestDTO, UserResponseDTO> {

  protected UserRepository userRepository;

  public UserInteractor(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
}
