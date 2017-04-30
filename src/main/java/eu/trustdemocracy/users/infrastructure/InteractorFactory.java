package eu.trustdemocracy.users.infrastructure;

import eu.trustdemocracy.users.core.interactors.AuthInteractor;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.interactors.UserInteractor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;

public interface InteractorFactory {

  Interactor<UserRequestDTO, UserResponseDTO> createUserInteractor(
      Class<? extends UserInteractor> concreteClass);

  Interactor<UserRequestDTO, String> createAuthInteractor(
      Class<? extends AuthInteractor> concreteClass);
}
