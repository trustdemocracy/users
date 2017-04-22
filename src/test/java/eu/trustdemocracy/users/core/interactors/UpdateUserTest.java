package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.fake.FakeUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateUserTest {

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
    public void updateSingleUser() {
        UserRequestDTO inputUser = inputUsers.get(0).setSurname("TestSurname");
        UserResponseDTO responseUser = new CreateUser(userDAO).execute(inputUser);
        assertEquals(null, responseUser.getName());

        UserResponseDTO expectedUser = new UserResponseDTO()
                .setUsername(inputUser.getUsername())
                .setEmail(inputUser.getEmail())
                .setName("TestName")
                .setId(responseUser.getId());

        UpdateUser interactor = new UpdateUser(userDAO);
        responseUser = interactor.execute(inputUser.setName("TestName").setSurname(null));

        assertEquals(responseUser, expectedUser);
    }
}
