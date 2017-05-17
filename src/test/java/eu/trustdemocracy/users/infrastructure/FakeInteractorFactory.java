package eu.trustdemocracy.users.infrastructure;

import com.github.fakemongo.Fongo;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.interactors.UserInteractor;
import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.interactors.auth.RefreshToken;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.TokenDAO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.mongo.MongoTokenDAO;
import eu.trustdemocracy.users.gateways.mongo.MongoUserDAO;
import lombok.val;

public class FakeInteractorFactory implements InteractorFactory {

  private UserDAO userDAO;
  private TokenDAO tokenDAO;

  @Override
  public Interactor<UserRequestDTO, UserResponseDTO> createUserInteractor(
      Class<? extends UserInteractor> concreteClass) {
    try {
      val constructor = concreteClass.getConstructor(UserDAO.class);
      val userDAO = getFakeUserDAO();
      return constructor.newInstance(userDAO);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public GetToken createGetTokenInteractor() {
    return new GetToken(getFakeUserDAO(), getFakeTokenDAO());
  }

  @Override
  public RefreshToken createRefreshTokenInteractor() {
    return new RefreshToken(getFakeUserDAO(), getFakeTokenDAO());
  }

  private UserDAO getFakeUserDAO() {
    if (userDAO == null) {
      val fongo = new Fongo("test server");
      val db = fongo.getDatabase("test_database");
      userDAO = new MongoUserDAO(db);
    }
    return userDAO;
  }

  private TokenDAO getFakeTokenDAO() {
    if (tokenDAO == null) {
      val fongo = new Fongo("test server");
      val db = fongo.getDatabase("test_database");
      tokenDAO = new MongoTokenDAO(db);
    }
    return tokenDAO;
  }
}
