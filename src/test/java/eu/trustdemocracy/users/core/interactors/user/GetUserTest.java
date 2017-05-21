package eu.trustdemocracy.users.core.interactors.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.fake.FakeUserDAO;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetUserTest {
  private static Map<UUID, UserResponseDTO> responseUsers;
  private UserDAO userDAO;

  @BeforeEach
  public void init() {
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
  public void getSingleUser() {
    val responseUser = responseUsers.values().iterator().next();
    val inputUser = new UserRequestDTO()
        .setId(responseUser.getId());

    assertEquals(responseUser, new GetUser(userDAO).execute(inputUser));
  }

  @Test
  public void getUserByUsername() {
    val responseUser = responseUsers.values().iterator().next();
    val inputUser = new UserRequestDTO()
        .setUsername(responseUser.getUsername());

    assertEquals(responseUser, new GetUser(userDAO).execute(inputUser));
  }

}
