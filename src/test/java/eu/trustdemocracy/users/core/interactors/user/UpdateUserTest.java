package eu.trustdemocracy.users.core.interactors.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.trustdemocracy.users.core.entities.UserVisibility;
import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
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

public class UpdateUserTest {
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
  public void updateSingleUser() {
    val responseUser = responseUsers.values().iterator().next();
    val inputUser = new UserRequestDTO()
        .setId(responseUser.getId())
        .setUsername(responseUser.getUsername())
        .setEmail(responseUser.getEmail())
        .setName(null)
        .setSurname("TestSurname");

    val expectedUser = new UserResponseDTO()
        .setUsername(inputUser.getUsername())
        .setEmail(inputUser.getEmail())
        .setName(responseUser.getName())
        .setSurname(inputUser.getSurname())
        .setId(inputUser.getId())
        .setVisibility(responseUser.getVisibility());

    val interactor = new UpdateUser(userDAO);
    val resultUser = interactor.execute(inputUser);

    assertEquals(expectedUser, resultUser);
  }

  @Test
  public void updateSeveralUsers() {
    val interactor = new UpdateUser(userDAO);
    for (val responseUser : responseUsers.values()) {
      val inputUser = new UserRequestDTO()
          .setId(responseUser.getId())
          .setUsername(responseUser.getUsername())
          .setEmail(responseUser.getEmail())
          .setName("")
          .setSurname("TestSurname");

      val expectedUser = new UserResponseDTO()
          .setUsername(inputUser.getUsername())
          .setEmail(inputUser.getEmail())
          .setName(inputUser.getName())
          .setSurname(inputUser.getSurname())
          .setId(inputUser.getId())
          .setVisibility(responseUser.getVisibility());

      val resultUser = interactor.execute(inputUser);

      assertEquals(expectedUser, resultUser);
    }
  }

  @Test
  public void noUpdateUsername() {
    val responseUser = responseUsers.values().iterator().next();
    val inputUser = new UserRequestDTO()
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
  public void updateUnexistingUser() {
    UUID id;
    do {
      id = UUID.randomUUID();
    } while (responseUsers.containsKey(id));

    val inputUser = new UserRequestDTO()
        .setId(id);

    assertThrows(IllegalStateException.class, () -> new UpdateUser(userDAO).execute(inputUser));
  }

  @Test
  public void updateVisibility() {
    val responseUser = responseUsers.values().iterator().next();

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
    val responseUser = responseUsers.values().iterator().next();

    val inputUser = new UserRequestDTO()
        .setId(responseUser.getId())
        .setPassword("newPassword");
    new UpdateUser(userDAO).execute(inputUser);

    val userInDB = userDAO.findById(responseUser.getId());

    assertTrue(CryptoUtils.validate(userInDB.getPassword(), "newPassword"));
  }

}
