package eu.trustdemocracy.users.core.interactors.auth;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.AuthInteractor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import lombok.val;

public class GetToken extends AuthInteractor {

  public GetToken(UserDAO userDAO) {
    super(userDAO);
  }

  @Override
  public String execute(UserRequestDTO userRequestDTO) {
    val user = getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
    if (user == null) {
      return null;
    }

    return UserMapper.createToken(user);
  }

  private User getUser(String username, String password) {
    val user = userDAO.findByUsername(username);

    if (user != null && CryptoUtils.validate(user.getPassword(), password)) {
      return user;
    }

    return null;
  }
}

