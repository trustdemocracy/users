package eu.trustdemocracy.users.core.interactors.auth;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
import eu.trustdemocracy.users.core.interactors.AuthInteractor;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import lombok.val;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

public class GetToken extends AuthInteractor {

  private static final Logger LOG = LoggerFactory.getLogger(GetToken.class);

  public GetToken(UserDAO userDAO) {
    super(userDAO);
  }

  @Override
  public String execute(UserRequestDTO userRequestDTO) {
    val user = getUser(userRequestDTO.getUsername(), userRequestDTO.getPassword());
    if (user == null) {
      return null;
    }

    return createToken(user);
  }

  private User getUser(String username, String password) {
    val user = userDAO.findByUsername(username);

    if (user != null && CryptoUtils.validate(user.getPassword(), password)) {
      return user;
    }

    return null;
  }

  private String createToken(User user) {
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

