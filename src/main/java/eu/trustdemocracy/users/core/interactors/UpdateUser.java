package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public class UpdateUser {
    public UpdateUser(UserDAO userDAO) {
    }

    public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
        return null;
    }
}
