package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.interactors.exceptions.UsernameAlreadyExistsException;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.fake.FakeUserDAO;
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    public static void initAll() {
        inputUsers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            inputUsers.add(new UserRequestDTO()
                    .setUsername("user" + i)
                    .setEmail("user" + i + "@user.com")
                    .setPassword("test" + i));
        }
    }

    @BeforeEach
    public void init() {
        userDAO = new FakeUserDAO();
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


}
