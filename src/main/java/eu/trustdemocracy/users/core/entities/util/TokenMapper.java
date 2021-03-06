package eu.trustdemocracy.users.core.entities.util;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.core.models.response.GetTokenResponseDTO;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import lombok.val;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

public class TokenMapper {

  private static final Logger LOG = LoggerFactory.getLogger(TokenMapper.class);

  public static GetTokenResponseDTO createResponse(User user, String refreshToken) {
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
    claims.setClaim("rank", user.getRank());

    JsonWebSignature jws = new JsonWebSignature();
    jws.setPayload(claims.toJson());
    jws.setKey(JWTKeyFactory.getPrivateKey());
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

    try {
      val token = jws.getCompactSerialization();
      return new GetTokenResponseDTO()
          .setAccessToken(token)
          .setRefreshToken(refreshToken);
    } catch (JoseException e) {
      LOG.error("Failed to get serialized jwt", e);
      return null;
    }
  }
}
