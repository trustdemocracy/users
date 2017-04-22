package eu.trustdemocracy.users.core.interactors;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.interactors.exceptions.UsernameAlreadyExistsException;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;

public class CreateUser {
    private UserDAO userDAO;

    public CreateUser(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
        validateUserState(userRequestDTO);

        if (userDAO.findWithUsername(userRequestDTO.getUsername()) != null) {
            throw new UsernameAlreadyExistsException("The username [" + userRequestDTO.getUsername() + "] already exists");
        }

        User user = userDAO.createUser(createUser(userRequestDTO));
        return createUserResponse(user);
    }

    private void validateUserState(UserRequestDTO user) {
        if (user.getUsername().isEmpty()) {
            throw new IllegalStateException("The username cannot be empty");
        }

        if (user.getEmail().isEmpty()) {
            throw new IllegalStateException("The email cannot be empty");
        }
    }

    private User createUser(UserRequestDTO userRequestDTO) {

        return new User()
                .setUsername(userRequestDTO.getUsername())
                .setEmail(userRequestDTO.getEmail())
                .setPassword(userRequestDTO.getPassword());
    }

    private UserResponseDTO createUserResponse(User user) {
        return new UserResponseDTO()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail());
    }
}
