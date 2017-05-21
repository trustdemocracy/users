package eu.trustdemocracy.users.core.interactors.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.interactors.utils.TokenUtils;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.GetUsersResponseDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.fake.FakeTokenDAO;
import eu.trustdemocracy.users.gateways.fake.FakeUserDAO;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetUsersTest {
  private static Map<UUID, UserResponseDTO> responseUsers;
  private UserDAO userDAO;

  @BeforeEach
  public void init() {
    TokenUtils.generateKeys();
    userDAO = new FakeUserDAO();
    responseUsers = new HashMap<>();

    val interactor = new CreateUser(userDAO);
    for (int i = 0; i < 10; i++) {
      val inputUser = new UserRequestDTO()
          .setUsername("user" + i)
          .setEmail("user" + i + "@user.com")
          .setPassword("test" + i)
          .setName("Name" + i);

      val responseUser = interactor.execute(inputUser);
      responseUsers.put(responseUser.getId(), responseUser);
    }
  }

  @Test
  public void getUsers() {
    val getToken = new GetToken(userDAO, new FakeTokenDAO());

    val accessToken = getToken.execute(new UserRequestDTO()
        .setUsername("user1")
        .setPassword("test1"));

    val inputUser = new UserRequestDTO()
        .setAccessToken(accessToken.getAccessToken());

    GetUsersResponseDTO response = new GetUsers(userDAO).execute(inputUser);
    assertEquals(responseUsers.values().size(), response.getUsers().size());
  }

}
