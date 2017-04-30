package eu.trustdemocracy.users.core.interactors.auth;

import eu.trustdemocracy.users.core.interactors.AuthInteractor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public class GetToken extends AuthInteractor {

  public GetToken(UserDAO userDAO) {
    super(userDAO);
  }

  @Override
  public String execute(UserRequestDTO userRequestDTO) {
    return null;
  }
}

