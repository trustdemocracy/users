package eu.trustdemocracy.users.infrastructure;

import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import lombok.val;

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
  public Interactor<UserRequestDTO, UserResponseDTO> createUserInteractor(
      Class<? extends Interactor<UserRequestDTO, UserResponseDTO>> concreteClass)
      throws ReflectiveOperationException {

    val constructor = concreteClass.getConstructor(UserDAO.class);
    val userDAO = DAOFactory.getUserDAO();
    return constructor.newInstance(userDAO);
  }
}
