package eu.trustdemocracy.users.infrastructure;

import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;

public interface InteractorFactory {

  Interactor<UserRequestDTO, UserResponseDTO> createUserInteractor(
      Class<? extends Interactor<UserRequestDTO, UserResponseDTO>> concreteClass);
}
