package eu.trustdemocracy.users.core.entities.util;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.UserVisibility;
import eu.trustdemocracy.users.core.interactors.exceptions.InvalidTokenException;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import java.util.Map;
import java.util.UUID;
import lombok.val;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

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

      if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
        user.setPassword(userRequestDTO.getPassword());
      }
    }

    return user;
  }

  public static User createEntity(String token) {
    val claims = getClaimsMap(token);

    val name = String.valueOf(claims.get("name"));
    val surname = String.valueOf(claims.get("surname"));

    val user = new User()
        .setId(UUID.fromString(String.valueOf(claims.get("sub"))))
        .setUsername(String.valueOf(claims.get("username")))
        .setEmail(String.valueOf(claims.get("email")))
        .setVisibility(UserVisibility.valueOf(String.valueOf(claims.get("visibility"))));

    if (!name.equals("null")) {
      user.setName(name);
    }
    if (!surname.equals("null")) {
      user.setSurname(name);
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


  private static Map<String, Object> getClaimsMap(String token) {
    val jwtConsumer = new JwtConsumerBuilder()
        .setRequireExpirationTime()
        .setAllowedClockSkewInSeconds(30)
        .setRequireSubject()
        .setVerificationKey(JWTKeyFactory.getPublicKey())
        .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
            AlgorithmIdentifiers.RSA_USING_SHA256))
        .build();

    try {
      val jwtClaims = jwtConsumer.processToClaims(token);
      return jwtClaims.getClaimsMap();
    } catch (InvalidJwtException e) {
      throw new InvalidTokenException(
          "The access token provided is not valid. Access token: [" + token + "]");
    }
  }
}
