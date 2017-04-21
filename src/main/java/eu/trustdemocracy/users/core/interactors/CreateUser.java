package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public class CreateUser {

    public CreateUser(UserDAO userDAO) {
    }

    public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
        return null;
    }
}
