package eu.trustdemocracy.users.core.interactors.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

public class DeleteUserTest {

  private static Map<String, UserResponseDTO> responseUsers;
  private UserDAO userDAO;

  @BeforeEach
  public void init() {
    TokenUtils.generateKeys();

    userDAO = new FakeUserDAO();
    responseUsers = new HashMap<>();

    val createUser = new CreateUser(userDAO);
    val getToken = new GetToken(userDAO, new FakeTokenDAO());
    for (int i = 0; i < 10; i++) {
      val inputUser = new UserRequestDTO()
          .setUsername("user" + i)
          .setEmail("user" + i + "@user.com")
          .setPassword("test" + i)
          .setName("Name" + i);

      val responseUser = createUser.execute(inputUser);
      val accessToken = getToken.execute(inputUser);
      responseUsers.put(accessToken.getAccessToken(), responseUser);
    }
  }

  @Test
  public void deleteNotAuthorizedUser() {
    val responseUser = responseUsers.values().iterator().next();
    val inputUser = new UserRequestDTO()
        .setId(responseUser.getId());

    assertThrows(InvalidTokenException.class, () -> new DeleteUser(userDAO).execute(inputUser));
  }

  @Test
  public void deleteSingleUser() {
    val accessToken = responseUsers.keySet().iterator().next();
    val responseUser = responseUsers.get(accessToken);
    val inputUser = new UserRequestDTO()
        .setAccessToken(accessToken);

    new DeleteUser(userDAO).execute(inputUser);

    assertEquals(null, userDAO.findById(responseUser.getId()));
  }

  @Test
  public void deleteSeveralUsers() {
    val interactor = new DeleteUser(userDAO);

    for (val accessToken : responseUsers.keySet()) {
      val responseUser = responseUsers.get(accessToken);
      val inputUser = new UserRequestDTO()
          .setAccessToken(accessToken);

      interactor.execute(inputUser);

      assertEquals(null, userDAO.findById(responseUser.getId()));
    }
  }
}
