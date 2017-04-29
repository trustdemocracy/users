package eu.trustdemocracy.users.core.entities.util;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import lombok.val;

public final class UserMapper {

  public static User createEntity(UserRequestDTO userRequestDTO) {
    val user = new User();

    if (userRequestDTO != null) {
      user
          .setId(userRequestDTO.getId())
          .setUsername(userRequestDTO.getUsername())
          .setEmail(userRequestDTO.getEmail())
          .setName(userRequestDTO.getName())
          .setSurname(userRequestDTO.getSurname())
          .setVisibility(userRequestDTO.getVisibility());

      if (userRequestDTO.getPassword() != null || !userRequestDTO.getPassword().isEmpty()) {
        user.setPassword(userRequestDTO.getPassword());
      }
    }

    return user;
  }

  public static UserResponseDTO createResponse(User user) {
    val userResponse = new UserResponseDTO();

    if (user != null) {
      userResponse
          .setId(user.getId())
          .setUsername(user.getUsername())
          .setEmail(user.getEmail())
          .setName(user.getName())
          .setSurname(user.getSurname())
          .setVisibility(user.getVisibility());
    }

    return userResponse;
  }
}
