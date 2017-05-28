package eu.trustdemocracy.users.core.interactors.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.trustdemocracy.users.core.entities.UserVisibility;
import eu.trustdemocracy.users.core.interactors.exceptions.UsernameAlreadyExistsException;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.fake.FakeUserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateUserTest {
  private static List<UserRequestDTO> inputUsers;
  private FakeUserRepository userDAO;

  @BeforeEach
  public void init() {
    userDAO = new FakeUserRepository();
    inputUsers = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      inputUsers.add(new UserRequestDTO()
          .setUsername("user" + i)
          .setEmail("user" + i + "@user.com")
          .setPassword("test" + i));
    }
  }

  @Test
  public void createSingleUser() {
    val uuid = userDAO.getUniqueUUID();
    val inputUser = inputUsers.get(0);

    val expectedUser = new UserResponseDTO()
        .setUsername(inputUser.getUsername())
        .setEmail(inputUser.getEmail())
        .setId(uuid)
        .setVisibility(UserVisibility.PRIVATE);

    val interactor = new CreateUser(userDAO);
    val responseUser = interactor.execute(inputUser);

    assertEquals(responseUser, expectedUser);
  }

  @Test
  public void createFullUser() {
    val uuid = userDAO.getUniqueUUID();
    val inputUser = inputUsers.get(0)
        .setName("TestName")
        .setSurname("TestSurname");

    val expectedUser = new UserResponseDTO()
        .setUsername(inputUser.getUsername())
        .setEmail(inputUser.getEmail())
        .setName(inputUser.getName())
        .setSurname(inputUser.getSurname())
        .setId(uuid)
        .setVisibility(UserVisibility.PRIVATE);

    val interactor = new CreateUser(userDAO);
    val responseUser = interactor.execute(inputUser);

    assertEquals(responseUser, expectedUser);
  }

  @Test
  public void createSeveralUsers() {
    val interactor = new CreateUser(userDAO);

    for (UserRequestDTO inputUser : inputUsers) {
      val uuid = userDAO.getUniqueUUID();
      val expectedUser = new UserResponseDTO()
          .setUsername(inputUser.getUsername())
          .setEmail(inputUser.getEmail())
          .setId(uuid)
          .setVisibility(UserVisibility.PRIVATE);
      val responseUser = interactor.execute(inputUser);

      assertEquals(responseUser, expectedUser);
    }
  }

  @Test
  public void createWithExistingUsername() {
    createSingleUser();
    assertThrows(UsernameAlreadyExistsException.class, this::createSingleUser);
  }

  @Test
  public void createWithEmptyUsername() {
    val inputUser = inputUsers.get(0).setUsername("");
    assertThrows(IllegalStateException.class, () -> new CreateUser(userDAO).execute(inputUser));
  }

  @Test
  public void createWithEmptyEmail() {
    val inputUser = inputUsers.get(0).setEmail("");
    assertThrows(IllegalStateException.class, () -> new CreateUser(userDAO).execute(inputUser));
  }

  @Test
  public void createWithEmptyPassword() {
    val inputUser = inputUsers.get(0).setPassword("");
    assertThrows(IllegalStateException.class, () -> new CreateUser(userDAO).execute(inputUser));
  }
}
