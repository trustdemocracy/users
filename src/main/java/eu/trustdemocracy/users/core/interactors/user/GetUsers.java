package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.GetUsersResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public class GetUsers implements Interactor<UserRequestDTO, GetUsersResponseDTO> {

  public GetUsers(UserDAO userDAO) {
  }

  @Override
  public GetUsersResponseDTO execute(UserRequestDTO requestDTO) {
    return null;
  }
}
