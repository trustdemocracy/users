package eu.trustdemocracy.users.core.interactors.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.trustdemocracy.users.core.interactors.exceptions.UserNotFoundException;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.out.FakeMainGateway;
import eu.trustdemocracy.users.gateways.repositories.UserRepository;
import eu.trustdemocracy.users.gateways.repositories.fake.FakeUserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetUserTest {
  private static Map<UUID, UserResponseDTO> responseUsers;
  private UserRepository userRepository;

  @BeforeEach
  public void init() {
    userRepository = new FakeUserRepository();
    responseUsers = new HashMap<>();

    val interactor = new CreateUser(userRepository, new FakeMainGateway());
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

    assertEquals(responseUser, new GetUser(userRepository).execute(inputUser));
  }

  @Test
  public void getUserByUsername() {
    val responseUser = responseUsers.values().iterator().next();
    val inputUser = new UserRequestDTO()
        .setUsername(responseUser.getUsername());

    assertEquals(responseUser, new GetUser(userRepository).execute(inputUser));
  }

  @Test
  public void getNonExistingUser() {
    val inputUser = new UserRequestDTO()
        .setUsername("IDontExist");

    assertThrows(UserNotFoundException.class, () -> new GetUser(userRepository).execute(inputUser));
  }

}
