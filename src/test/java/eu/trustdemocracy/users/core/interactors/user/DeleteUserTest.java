package eu.trustdemocracy.users.core.interactors.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.interactors.exceptions.InvalidTokenException;
import eu.trustdemocracy.users.core.interactors.utils.TokenUtils;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.out.FakeMainGateway;
import eu.trustdemocracy.users.gateways.repositories.UserRepository;
import eu.trustdemocracy.users.gateways.repositories.fake.FakeTokenRepository;
import eu.trustdemocracy.users.gateways.repositories.fake.FakeUserRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeleteUserTest {

  private static Map<String, UserResponseDTO> responseUsers;
  private UserRepository userRepository;
  private FakeMainGateway mainGateway;

  @BeforeEach
  public void init() {
    TokenUtils.generateKeys();

    userRepository = new FakeUserRepository();
    mainGateway = new FakeMainGateway();
    responseUsers = new HashMap<>();

    val createUser = new CreateUser(userRepository, mainGateway);
    val getToken = new GetToken(userRepository, new FakeTokenRepository());
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

    assertThrows(InvalidTokenException.class,
        () -> new DeleteUser(userRepository, mainGateway).execute(inputUser));
  }

  @Test
  public void deleteSingleUser() {
    val accessToken = responseUsers.keySet().iterator().next();
    val responseUser = responseUsers.get(accessToken);
    val inputUser = new UserRequestDTO()
        .setAccessToken(accessToken);

    new DeleteUser(userRepository, mainGateway).execute(inputUser);

    assertEquals(null, userRepository.findById(responseUser.getId()));
  }

  @Test
  public void deleteSeveralUsers() {
    val interactor = new DeleteUser(userRepository, mainGateway);

    for (val accessToken : responseUsers.keySet()) {
      val responseUser = responseUsers.get(accessToken);
      val inputUser = new UserRequestDTO()
          .setAccessToken(accessToken);

      interactor.execute(inputUser);

      assertEquals(null, userRepository.findById(responseUser.getId()));
    }
  }
}
