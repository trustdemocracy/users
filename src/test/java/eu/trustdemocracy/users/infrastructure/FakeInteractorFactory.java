package eu.trustdemocracy.users.infrastructure;

import com.github.fakemongo.Fongo;
import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.interactors.auth.RefreshToken;
import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.interactors.user.DeleteUser;
import eu.trustdemocracy.users.core.interactors.user.GetUser;
import eu.trustdemocracy.users.core.interactors.user.GetUsers;
import eu.trustdemocracy.users.core.interactors.user.UpdateUser;
import eu.trustdemocracy.users.gateways.out.FakeMainGateway;
import eu.trustdemocracy.users.gateways.out.MainGateway;
import eu.trustdemocracy.users.gateways.repositories.TokenRepository;
import eu.trustdemocracy.users.gateways.repositories.UserRepository;
import eu.trustdemocracy.users.gateways.repositories.mongo.MongoTokenRepository;
import eu.trustdemocracy.users.gateways.repositories.mongo.MongoUserRepository;
import lombok.val;

public class FakeInteractorFactory implements InteractorFactory {

  private UserRepository userRepository;
  private TokenRepository tokenRepository;

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
    if (userRepository == null) {
      val fongo = new Fongo("test server");
      val db = fongo.getDatabase("test_database");
      userRepository = new MongoUserRepository(db);
    }
    return userRepository;
  }

  private TokenRepository getTokenRepository() {
    if (tokenRepository == null) {
      val fongo = new Fongo("test server");
      val db = fongo.getDatabase("test_database");
      tokenRepository = new MongoTokenRepository(db);
    }
    return tokenRepository;
  }

  private MainGateway getMainGateway() {
    return new FakeMainGateway();
  }
}
