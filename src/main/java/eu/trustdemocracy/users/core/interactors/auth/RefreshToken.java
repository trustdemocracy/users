package eu.trustdemocracy.users.core.interactors.auth;

import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
import eu.trustdemocracy.users.core.entities.util.TokenMapper;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.models.request.RefreshTokenRequestDTO;
import eu.trustdemocracy.users.core.models.response.GetTokenResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import lombok.val;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

public class RefreshToken implements Interactor<RefreshTokenRequestDTO, GetTokenResponseDTO> {

  private UserDAO userDAO;

  public RefreshToken(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public GetTokenResponseDTO execute(RefreshTokenRequestDTO requestDTO) {
    val jwtConsumer = new JwtConsumerBuilder()
        .setRequireExpirationTime()
        .setAllowedClockSkewInSeconds(30)
        .setRequireSubject()
        .setVerificationKey(JWTKeyFactory.getPublicKey())
        .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
            AlgorithmIdentifiers.RSA_USING_SHA256))
        .build();

    try {
      val jwtClaims = jwtConsumer.processToClaims(requestDTO.getAccessToken());
      val claims = jwtClaims.getClaimsMap();

      val user = userDAO.findByUsername(String.valueOf(claims.get("username")));

      val refreshToken = CryptoUtils.randomToken();

      return TokenMapper.createResponse(user, refreshToken);
    } catch (InvalidJwtException e) {
      throw new RuntimeException(e);
    }
  }
}
