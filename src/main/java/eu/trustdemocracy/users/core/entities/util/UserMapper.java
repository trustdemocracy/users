package eu.trustdemocracy.users.core.entities.util;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import lombok.val;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

public final class UserMapper {

  private static final Logger LOG = LoggerFactory.getLogger(UserMapper.class);

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

  public static String createToken(User user) {
    val claims = new JwtClaims();
    claims.setExpirationTimeMinutesInTheFuture(10);
    claims.setGeneratedJwtId();
    claims.setIssuedAtToNow();
    claims.setNotBeforeMinutesInThePast(2);

    claims.setSubject(user.getId().toString());
    claims.setClaim("username", user.getUsername());
    claims.setClaim("email", user.getEmail());
    claims.setClaim("name", user.getName());
    claims.setClaim("surname", user.getSurname());
    claims.setClaim("visibility", user.getVisibility());

    JsonWebSignature jws = new JsonWebSignature();
    jws.setPayload(claims.toJson());
    jws.setKey(JWTKeyFactory.getPrivateKey());
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

    try {
      return jws.getCompactSerialization();
    } catch (JoseException e) {
      LOG.error("Failed to get jwt", e);
      return null;
    }
  }
}
