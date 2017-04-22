package eu.trustdemocracy.users.core.interactors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.UserVisibility;
import eu.trustdemocracy.users.core.entities.utils.CryptoUtils;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.fake.FakeUserDAO;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateUserTest {

  private static Map<UUID, UserResponseDTO> responseUsers;
  private UserDAO userDAO;

  @BeforeEach
  public void init() {
    userDAO = new FakeUserDAO();
    responseUsers = new HashMap<>();

    CreateUser interactor = new CreateUser(userDAO);
    for (int i = 0; i < 10; i++) {
      UserRequestDTO inputUser = new UserRequestDTO()
          .setUsername("user" + i)
          .setEmail("user" + i + "@user.com")
          .setPassword("test" + i)
          .setName("Name" + i);

      UserResponseDTO responseUser = interactor.execute(inputUser);
      responseUsers.put(responseUser.getId(), responseUser);
    }
  }

  @Test
  public void updateSingleUser() {
    UserResponseDTO responseUser = responseUsers.values().iterator().next();
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
  public void updateSeveralUsers() {
    for (UserResponseDTO responseUser : responseUsers.values()) {
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
  }

  @Test
  public void noUpdateUsername() {
    UserResponseDTO responseUser = responseUsers.values().iterator().next();
    UserRequestDTO inputUser = new UserRequestDTO()
        .setId(responseUser.getId())
        .setUsername("NewUsername");

    UserResponseDTO expectedUser = new UserResponseDTO()
        .setId(responseUser.getId())
        .setUsername(responseUser.getUsername());

    assertEquals(expectedUser, new UpdateUser(userDAO).execute(inputUser));
  }

  @Test
  public void updateUnexistingUser() {
    UUID id;
    do {
      id = UUID.randomUUID();
    } while (responseUsers.containsKey(id));

    UserRequestDTO inputUser = new UserRequestDTO()
        .setId(id);

    assertThrows(IllegalStateException.class, () -> new UpdateUser(userDAO).execute(inputUser));
  }

  @Test
  public void updateVisibility() {
    UserResponseDTO responseUser = responseUsers.values().iterator().next();

    UserRequestDTO inputUser = new UserRequestDTO()
        .setId(responseUser.getId())
        .setVisibility(UserVisibility.PUBLIC);
    assertEquals(UserVisibility.PUBLIC, new UpdateUser(userDAO).execute(inputUser).getVisibility());

    inputUser = new UserRequestDTO()
        .setId(responseUser.getId())
        .setVisibility(UserVisibility.NORMAL);
    assertEquals(UserVisibility.NORMAL, new UpdateUser(userDAO).execute(inputUser).getVisibility());

    inputUser = new UserRequestDTO()
        .setId(responseUser.getId())
        .setVisibility(UserVisibility.PRIVATE);
    assertEquals(UserVisibility.PRIVATE, new UpdateUser(userDAO).execute(inputUser).getVisibility());
  }

  @Test
  public void updatePassword() {
    UserResponseDTO responseUser = responseUsers.values().iterator().next();

    UserRequestDTO inputUser = new UserRequestDTO()
        .setId(responseUser.getId())
        .setPassword("newPassword");
    new UpdateUser(userDAO).execute(inputUser);

    User userInDB = userDAO.findById(responseUser.getId());

    assertTrue(CryptoUtils.validate(userInDB.getPassword(), "newPassword"));
  }

}
