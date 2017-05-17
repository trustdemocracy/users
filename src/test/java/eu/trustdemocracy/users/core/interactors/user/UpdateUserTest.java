package eu.trustdemocracy.users.core.interactors.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.trustdemocracy.users.core.entities.UserVisibility;
import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.interactors.exceptions.InvalidTokenException;
import eu.trustdemocracy.users.core.interactors.utils.TokenUtils;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.fake.FakeTokenDAO;
import eu.trustdemocracy.users.gateways.fake.FakeUserDAO;
import java.util.HashMap;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateUserTest {
  private static Map<String, UserResponseDTO> responseUsers;
  private UserDAO userDAO;

  @BeforeEach
  public void init() {
    TokenUtils.generateKeys();

    userDAO = new FakeUserDAO();
    responseUsers = new HashMap<>();

    val interactor = new CreateUser(userDAO);
    val getToken = new GetToken(userDAO, new FakeTokenDAO());
    for (int i = 0; i < 10; i++) {
      val inputUser = new UserRequestDTO()
          .setUsername("user" + i)
          .setEmail("user" + i + "@user.com")
          .setPassword("test" + i)
          .setName("Name" + i);

      val responseUser = interactor.execute(inputUser);
      val accessToken = getToken.execute(inputUser);
      responseUsers.put(accessToken.getAccessToken(), responseUser);
    }
  }

  @Test
  public void updateNotAuthorizedUser() {
    val responseUser = responseUsers.values().iterator().next();
    val inputUser = new UserRequestDTO()
        .setId(responseUser.getId())
        .setUsername(responseUser.getUsername())
        .setEmail(responseUser.getEmail())
        .setName(null)
        .setSurname("TestSurname");

    assertThrows(InvalidTokenException.class, () -> new UpdateUser(userDAO).execute(inputUser));
  }

  @Test
  public void updateSingleUser() {
    val accessToken = responseUsers.keySet().iterator().next();
    val responseUser = responseUsers.get(accessToken);
    val inputUser = new UserRequestDTO()
        .setAccessToken(accessToken)
        .setEmail(responseUser.getEmail())
        .setName(null)
        .setSurname("TestSurname");

    val expectedUser = new UserResponseDTO()
        .setId(responseUser.getId())
        .setUsername(responseUser.getUsername())
        .setEmail(inputUser.getEmail())
        .setName(responseUser.getName())
        .setSurname(inputUser.getSurname())
        .setVisibility(responseUser.getVisibility());

    val interactor = new UpdateUser(userDAO);
    val resultUser = interactor.execute(inputUser);

    assertEquals(expectedUser, resultUser);
  }

  @Test
  public void updateSeveralUsers() {
    val interactor = new UpdateUser(userDAO);
    for (val accessToken : responseUsers.keySet()) {
      val responseUser = responseUsers.get(accessToken);
      val inputUser = new UserRequestDTO()
          .setAccessToken(accessToken)
          .setEmail(responseUser.getEmail())
          .setName("")
          .setSurname("TestSurname");

      val expectedUser = new UserResponseDTO()
          .setId(responseUser.getId())
          .setUsername(responseUser.getUsername())
          .setEmail(inputUser.getEmail())
          .setName(inputUser.getName())
          .setSurname(inputUser.getSurname())
          .setVisibility(responseUser.getVisibility());

      val resultUser = interactor.execute(inputUser);

      assertEquals(expectedUser, resultUser);
    }
  }

  @Test
  public void noUpdateUsername() {
    val accessToken = responseUsers.keySet().iterator().next();
    val responseUser = responseUsers.get(accessToken);
    val inputUser = new UserRequestDTO()
        .setAccessToken(accessToken)
        .setId(responseUser.getId())
        .setUsername("NewUsername");

    val expectedUser = new UserResponseDTO()
        .setId(responseUser.getId())
        .setUsername(responseUser.getUsername())
        .setEmail(responseUser.getEmail())
        .setName(responseUser.getName())
        .setSurname(responseUser.getSurname())
        .setVisibility(responseUser.getVisibility());

    assertEquals(expectedUser, new UpdateUser(userDAO).execute(inputUser));
  }

  @Test
  public void updateVisibility() {
    val accessToken = responseUsers.keySet().iterator().next();

    UserRequestDTO inputUser = new UserRequestDTO()
        .setAccessToken(accessToken)
        .setVisibility(UserVisibility.PUBLIC);
    assertEquals(UserVisibility.PUBLIC, new UpdateUser(userDAO).execute(inputUser).getVisibility());

    inputUser = new UserRequestDTO()
        .setAccessToken(accessToken)
        .setVisibility(UserVisibility.NORMAL);
    assertEquals(UserVisibility.NORMAL, new UpdateUser(userDAO).execute(inputUser).getVisibility());

    inputUser = new UserRequestDTO()
        .setAccessToken(accessToken)
        .setVisibility(UserVisibility.PRIVATE);
    assertEquals(UserVisibility.PRIVATE, new UpdateUser(userDAO).execute(inputUser).getVisibility());
  }

  @Test
  public void updatePassword() {
    val accessToken = responseUsers.keySet().iterator().next();
    val responseUser = responseUsers.get(accessToken);

    val inputUser = new UserRequestDTO()
        .setAccessToken(accessToken)
        .setPassword("newPassword");
    new UpdateUser(userDAO).execute(inputUser);

    val userInDB = userDAO.findById(responseUser.getId());

    assertTrue(CryptoUtils.validate(userInDB.getPassword(), "newPassword"));
  }

}
