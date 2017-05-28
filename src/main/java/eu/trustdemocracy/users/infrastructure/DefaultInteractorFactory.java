package eu.trustdemocracy.users.infrastructure;

import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.interactors.auth.RefreshToken;
import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.interactors.user.DeleteUser;
import eu.trustdemocracy.users.core.interactors.user.GetUser;
import eu.trustdemocracy.users.core.interactors.user.GetUsers;
import eu.trustdemocracy.users.core.interactors.user.UpdateUser;
import eu.trustdemocracy.users.gateways.out.MainGateway;
import eu.trustdemocracy.users.gateways.out.MainGatewayImpl;
import eu.trustdemocracy.users.gateways.repositories.TokenRepository;
import eu.trustdemocracy.users.gateways.repositories.UserRepository;

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
    return new CreateUser(getUserRepository(), getMainGateway());
  }

  @Override
  public DeleteUser getDeleteUser() {
    return new DeleteUser(getUserRepository(), getMainGateway());
  }

  @Override
  public GetUser getGetUser() {
    return new GetUser(getUserRepository());
  }

  @Override
  public GetUsers getGetUsers() {
    return new GetUsers(getUserRepository());
  }

  @Override
  public UpdateUser getUpdateUser() {
    return new UpdateUser(getUserRepository());
  }

  @Override
  public GetToken getGetToken() {
    return new GetToken(getUserRepository(), getTokenRepository());
  }

  @Override
  public RefreshToken getRefreshToken() {
    return new RefreshToken(getUserRepository(), getTokenRepository());
  }

  private UserRepository getUserRepository() {
    return RepositoryFactory.getUserDAO();
  }

  private TokenRepository getTokenRepository() {
    return RepositoryFactory.getTokenDAO();
  }

  private MainGateway getMainGateway() {
    return new MainGatewayImpl();
  }
}
