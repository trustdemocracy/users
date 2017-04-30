package eu.trustdemocracy.users.core.interactors.auth;

import eu.trustdemocracy.users.core.entities.util.UserMapper;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import lombok.val;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

public class RefreshToken implements Interactor<String, String> {

  private UserDAO userDAO;

  public RefreshToken(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public String execute(String token) {
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
      val claims = jwtClaims.getClaimsMap();

      val user = userDAO.findByUsername(String.valueOf(claims.get("username")));

      return UserMapper.createToken(user);
    } catch (InvalidJwtException e) {
      throw new RuntimeException(e);
    }
  }
}
