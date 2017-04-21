package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.fake.FakeUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateUserTest {

    UserRequestDTO userRequestDTO;
    UserDAO userDAO;

    @BeforeEach
    public void init() {
        userRequestDTO = new UserRequestDTO()
                .setUsername("user")
                .setEmail("user@user.com")
                .setPassword("test");
        userDAO = new FakeUserDAO();
    }

    @Test
    public void createSingleUser() {
        UUID uuid = userDAO.getUniqueUUID();

        UserResponseDTO expectedUser = new UserResponseDTO()
                .setUsername("user")
                .setEmail("user@user.com")
                .setId(uuid);

        CreateUser interactor = new CreateUser(userDAO);
        UserResponseDTO userResponseDTO = interactor.execute(userRequestDTO);

        assertEquals(userResponseDTO, expectedUser);
    }
}
