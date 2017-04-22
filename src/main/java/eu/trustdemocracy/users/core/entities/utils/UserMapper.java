package eu.trustdemocracy.users.core.entities.utils;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;

public final class UserMapper {
    public static User createEntity(UserRequestDTO userRequestDTO) {
        return new User()
                .setId(userRequestDTO.getId())
                .setUsername(userRequestDTO.getUsername())
                .setEmail(userRequestDTO.getEmail())
                .setPassword(userRequestDTO.getPassword())
                .setName(userRequestDTO.getName())
                .setSurname(userRequestDTO.getSurname());
    }

    public static UserResponseDTO createResponse(User user) {
        return new UserResponseDTO()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setName(user.getName())
                .setSurname(user.getSurname());
    }
}
