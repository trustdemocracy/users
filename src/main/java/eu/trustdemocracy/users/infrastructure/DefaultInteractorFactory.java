package eu.trustdemocracy.users.infrastructure;

import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.interactors.auth.RefreshToken;
import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.interactors.user.DeleteUser;
import eu.trustdemocracy.users.core.interactors.user.GetUser;
import eu.trustdemocracy.users.core.interactors.user.GetUsers;
import eu.trustdemocracy.users.core.interactors.user.UpdateUser;
import eu.trustdemocracy.users.gateways.TokenRepository;
import eu.trustdemocracy.users.gateways.UserRepository;

public class DefaultInteractorFactory implements InteractorFactory {

  private static DefaultInteractorFactory instance;

  private DefaultInteractorFactory() {
  }

  public static DefaultInteractorFactory getInstance() {
    if (instance == null) {
      instance = new DefaultInteractorFactory();
    }
    return instance;
  }

  @Override
  public CreateUser getCreateUser() {
    return new CreateUser(getUserDAO());
  }

  @Override
  public DeleteUser getDeleteUser() {
    return new DeleteUser(getUserDAO());
  }

  @Override
  public GetUser getGetUser() {
    return new GetUser(getUserDAO());
  }

  @Override
  public GetUsers getGetUsers() {
    return new GetUsers(getUserDAO());
  }

  @Override
  public UpdateUser getUpdateUser() {
    return new UpdateUser(getUserDAO());
  }

  @Override
  public GetToken getGetToken() {
    return new GetToken(getUserDAO(), getTokenDAO());
  }

  @Override
  public RefreshToken getRefreshToken() {
    return new RefreshToken(getUserDAO(), getTokenDAO());
  }

  private UserRepository getUserDAO() {
    return RepositoryFactory.getUserDAO();
  }

  private TokenRepository getTokenDAO() {
    return RepositoryFactory.getTokenDAO();
  }
}
