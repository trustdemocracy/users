package eu.trustdemocracy.users.infrastructure;

import com.github.fakemongo.Fongo;
import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.interactors.auth.RefreshToken;
import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.interactors.user.DeleteUser;
import eu.trustdemocracy.users.core.interactors.user.GetUser;
import eu.trustdemocracy.users.core.interactors.user.GetUsers;
import eu.trustdemocracy.users.core.interactors.user.UpdateUser;
import eu.trustdemocracy.users.gateways.TokenDAO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.mongo.MongoTokenDAO;
import eu.trustdemocracy.users.gateways.mongo.MongoUserDAO;
import lombok.val;

public class FakeInteractorFactory implements InteractorFactory {

  private UserDAO userDAO;
  private TokenDAO tokenDAO;

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

  private UserDAO getUserDAO() {
    if (userDAO == null) {
      val fongo = new Fongo("test server");
      val db = fongo.getDatabase("test_database");
      userDAO = new MongoUserDAO(db);
    }
    return userDAO;
  }

  private TokenDAO getTokenDAO() {
    if (tokenDAO == null) {
      val fongo = new Fongo("test server");
      val db = fongo.getDatabase("test_database");
      tokenDAO = new MongoTokenDAO(db);
    }
    return tokenDAO;
  }
}
