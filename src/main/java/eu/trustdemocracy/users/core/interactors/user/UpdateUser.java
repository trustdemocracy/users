package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.UserInteractor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import lombok.val;

public class UpdateUser extends UserInteractor {

  public UpdateUser(UserDAO userDAO) {
    super(userDAO);
  }

  public UserResponseDTO execute(UserRequestDTO userRequestDTO) {
    val inputUser = UserMapper.createEntity(userRequestDTO.getAccessToken());
    userRequestDTO.setId(inputUser.getId());
    userRequestDTO.setUsername(inputUser.getUsername());


    val user = sanitizedUser(userRequestDTO);
    return UserMapper.createResponse(userDAO.update(user));
  }

  private User sanitizedUser(UserRequestDTO inputUser) {
    User user = userDAO.findById(inputUser.getId());
    if (user == null) {
      throw new IllegalStateException(
          "Trying to update unexisting user with id [" + inputUser.getId() + "]");
    }

    fillEmptyAttributesWithCurrentValues(user, inputUser);

    inputUser.setUsername(user.getUsername());

    val mappedUser = UserMapper.createEntity(inputUser);

    if (mappedUser.getPassword() == null || mappedUser.getPassword().isEmpty()) {
      mappedUser.setHashedPassword(user.getPassword());
    }

    return mappedUser;
  }

  private static void fillEmptyAttributesWithCurrentValues(User user, UserRequestDTO userRequestDTO) {
    if (userRequestDTO.getEmail() == null || userRequestDTO.getEmail().isEmpty()) {
      userRequestDTO.setEmail(user.getEmail());
    }

    if (userRequestDTO.getName() == null) {
      userRequestDTO.setName(user.getName());
    }

    if (userRequestDTO.getSurname() == null) {
      userRequestDTO.setSurname(user.getSurname());
    }

    if (userRequestDTO.getVisibility() == null) {
      userRequestDTO.setVisibility(user.getVisibility());
    }
  }
}
