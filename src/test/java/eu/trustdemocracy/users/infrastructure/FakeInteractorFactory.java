package eu.trustdemocracy.users.infrastructure;

import com.github.fakemongo.Fongo;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.interactors.UserInteractor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.mongo.MongoUserDAO;
import lombok.val;

public class FakeInteractorFactory implements InteractorFactory {

  private static UserDAO userDAO;

  @Override
  public Interactor<UserRequestDTO, UserResponseDTO> createUserInteractor(
      Class<? extends UserInteractor> concreteClass) {
    try {
      val constructor = concreteClass.getConstructor(UserDAO.class);
      val userDAO = getFakeDAO();
      return constructor.newInstance(userDAO);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  private UserDAO getFakeDAO() {
    if (userDAO == null) {
      val fongo = new Fongo("test server");
      val db = fongo.getDatabase("test_database");
      userDAO = new MongoUserDAO(db);
    }
    return userDAO;
  }
}
