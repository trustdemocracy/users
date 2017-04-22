package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.interactors.exceptions.UsernameAlreadyExistsException;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.fake.FakeUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateUserTest {

    private static List<UserRequestDTO> inputUsers;
    private UserDAO userDAO;

    @BeforeEach
    public void init() {
        userDAO = new FakeUserDAO();
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
        UUID uuid = userDAO.getUniqueUUID();
        UserRequestDTO inputUser = inputUsers.get(0);

        UserResponseDTO expectedUser = new UserResponseDTO()
                .setUsername(inputUser.getUsername())
                .setEmail(inputUser.getEmail())
                .setId(uuid);

        CreateUser interactor = new CreateUser(userDAO);
        UserResponseDTO responseUser = interactor.execute(inputUser);

        assertEquals(responseUser, expectedUser);
    }

    @Test
    public void createSeveralUsers() {
        CreateUser interactor = new CreateUser(userDAO);

        for (UserRequestDTO inputUser : inputUsers) {
            UUID uuid = userDAO.getUniqueUUID();
            UserResponseDTO expectedUser = new UserResponseDTO()
                    .setUsername(inputUser.getUsername())
                    .setEmail(inputUser.getEmail())
                    .setId(uuid);
            UserResponseDTO responseUser = interactor.execute(inputUser);

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
        UserRequestDTO inputUser = inputUsers.get(0).setUsername("");
        assertThrows(IllegalStateException.class, () -> new CreateUser(userDAO).execute(inputUser));
    }

    @Test
    public void createWithEmptyEmail() {
        UserRequestDTO inputUser = inputUsers.get(0).setEmail("");
        assertThrows(IllegalStateException.class, () -> new CreateUser(userDAO).execute(inputUser));
    }

    @Test
    public void createWithEmptyPassword() {
        UserRequestDTO inputUser = inputUsers.get(0).setPassword("");
        assertThrows(IllegalStateException.class, () -> new CreateUser(userDAO).execute(inputUser));
    }
}
