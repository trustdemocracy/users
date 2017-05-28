package eu.trustdemocracy.users.core.interactors.auth;

import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
import eu.trustdemocracy.users.core.entities.util.TokenMapper;
import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.interactors.exceptions.CredentialsNotFoundException;
import eu.trustdemocracy.users.core.models.request.RefreshTokenRequestDTO;
import eu.trustdemocracy.users.core.models.response.GetTokenResponseDTO;
import eu.trustdemocracy.users.gateways.repositories.TokenRepository;
import eu.trustdemocracy.users.gateways.repositories.UserRepository;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import java.util.UUID;
import lombok.val;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

public class RefreshToken implements Interactor<RefreshTokenRequestDTO, GetTokenResponseDTO> {

  private UserRepository userRepository;
  private TokenRepository tokenRepository;

  public RefreshToken(UserRepository userRepository, TokenRepository tokenRepository) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
  }

  @Override
  public GetTokenResponseDTO execute(RefreshTokenRequestDTO requestDTO) {
    val jwtConsumer = new JwtConsumerBuilder()
        .setSkipAllDefaultValidators()
        .setRequireSubject()
        .setVerificationKey(JWTKeyFactory.getPublicKey())
        .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
            AlgorithmIdentifiers.RSA_USING_SHA256))
        .build();

    try {
      val jwtClaims = jwtConsumer.processToClaims(requestDTO.getAccessToken());
      val claims = jwtClaims.getClaimsMap();
      val id = UUID.fromString(String.valueOf(claims.get("sub")));
      val username = String.valueOf(claims.get("username"));

      val found = tokenRepository.findAndDeleteRefreshToken(id, requestDTO.getRefreshToken());
      if (!found) {
        throw new CredentialsNotFoundException(
            "Invalid id or refresh token for user [" + username + "]");
      }

      val user = userRepository.findByUsername(username);

      val refreshToken = CryptoUtils.randomToken();
      tokenRepository.storeRefreshToken(id, refreshToken);

      return TokenMapper.createResponse(user, refreshToken);
    } catch (InvalidJwtException | IllegalArgumentException e) {
      throw new CredentialsNotFoundException("Failed to process jwt token");
    }
  }
}
