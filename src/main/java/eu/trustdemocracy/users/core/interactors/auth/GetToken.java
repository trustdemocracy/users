package eu.trustdemocracy.users.core.interactors.auth;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.interactors.exceptions.CredentialsNotFoundException;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import lombok.val;

public class GetToken implements Interactor<UserRequestDTO, String> {

  private UserDAO userDAO;

  public GetToken(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  /**
   * Returns a JWT token from a valid user with the user data and expiration date, and another token
   * for refreshing the one returned
   *
   * @param userRequestDTO the DTO containing the username and password
   * @return the JWT token containing and signing the user information
   * @throws CredentialsNotFoundException if the username doesn't exist or the password is wrong
   */
  @Override
  public String execute(UserRequestDTO userRequestDTO) {
    val username = userRequestDTO.getUsername();
    val user = getUser(username, userRequestDTO.getPassword());
    if (user == null) {
      throw new CredentialsNotFoundException(
          "Either password is wrong or username [" + username + "] doesn't exist");
    }

    return UserMapper.createToken(user);
  }

  /**
   * Returns a user by its username and a valid password
   *
   * @param username username of the user to be found
   * @param password password tied to that user
   * @return user in case of valid credentials and null whether the username doesn't exist or the
   * password is wrong
   */
  private User getUser(String username, String password) {
    val user = userDAO.findByUsername(username);
    if (user != null && CryptoUtils.validate(user.getPassword(), password)) {
      return user;
    }

    return null;
  }
}

