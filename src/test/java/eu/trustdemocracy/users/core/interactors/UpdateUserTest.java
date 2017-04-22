package eu.trustdemocracy.users.core.interactors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.fake.FakeUserDAO;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateUserTest {

  private static List<UserResponseDTO> responseUsers;
  private UserDAO userDAO;

  @BeforeEach
  public void init() {
    userDAO = new FakeUserDAO();
    responseUsers = new ArrayList<>();

    CreateUser interactor = new CreateUser(userDAO);
    for (int i = 0; i < 10; i++) {
      UserRequestDTO inputUser = new UserRequestDTO()
          .setUsername("user" + i)
          .setEmail("user" + i + "@user.com")
          .setPassword("test" + i)
          .setName("Name" + i);

      responseUsers.add(interactor.execute(inputUser));
    }
  }

  @Test
  public void updateSingleUser() {
    UserResponseDTO responseUser = responseUsers.get(0);
    UserRequestDTO inputUser = new UserRequestDTO()
        .setId(responseUser.getId())
        .setUsername(responseUser.getUsername())
        .setEmail(responseUser.getEmail())
        .setName(null)
        .setSurname("TestSurname");

    UserResponseDTO expectedUser = new UserResponseDTO()
        .setUsername(inputUser.getUsername())
        .setEmail(inputUser.getEmail())
        .setSurname(inputUser.getSurname())
        .setId(inputUser.getId());

    UpdateUser interactor = new UpdateUser(userDAO);
    responseUser = interactor.execute(inputUser);

    assertEquals(expectedUser, responseUser);
  }

  @Test
  public void updateUsername() {
    UserResponseDTO responseUser = responseUsers.get(0);
    UserRequestDTO inputUser = new UserRequestDTO()
        .setId(responseUser.getId())
        .setUsername("NewUsername");

    UserResponseDTO expectedUser = new UserResponseDTO()
        .setId(responseUser.getId())
        .setUsername(responseUser.getUsername());

    assertEquals(expectedUser, new UpdateUser(userDAO).execute(inputUser));
  }

}
