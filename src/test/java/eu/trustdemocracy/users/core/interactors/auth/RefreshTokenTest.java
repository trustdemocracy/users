package eu.trustdemocracy.users.core.interactors.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.GetTokenResponseDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.UserDAO;
import eu.trustdemocracy.users.gateways.fake.FakeUserDAO;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import java.util.HashMap;
import java.util.Map;
import lombok.val;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RefreshTokenTest {

  private static Map<GetTokenResponseDTO, UserResponseDTO> responseUsers;
  private UserDAO userDAO;
  private RsaJsonWebKey rsaJsonWebKey;

  @BeforeEach
  public void init() throws JoseException {
    rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
    JWTKeyFactory.setPrivateKey(rsaJsonWebKey.getPrivateKey());
    JWTKeyFactory.setPublicKey(rsaJsonWebKey.getPublicKey());

    userDAO = new FakeUserDAO();
    responseUsers = new HashMap<>();

    val interactor = new CreateUser(userDAO);
    for (int i = 0; i < 10; i++) {
      val inputUser = new UserRequestDTO()
          .setUsername("user" + i)
          .setEmail("user" + i + "@user.com")
          .setPassword("test" + i)
          .setName("Name" + i);

      val responseUser = interactor.execute(inputUser);
      GetTokenResponseDTO tokenDTO = new GetToken(userDAO).execute(inputUser);
      responseUsers.put(tokenDTO, responseUser);
    }
  }

  @Test
  public void refreshSingleToken() throws JoseException, InvalidJwtException {
    val issuedToken = responseUsers.keySet().iterator().next();
    val responseUser = responseUsers.get(issuedToken);

    GetTokenResponseDTO token = new RefreshToken(userDAO).execute(issuedToken.getJwtToken());

    val jwtConsumer = new JwtConsumerBuilder()
        .setRequireExpirationTime()
        .setAllowedClockSkewInSeconds(30)
        .setRequireSubject()
        .setVerificationKey(rsaJsonWebKey.getKey())
        .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
            AlgorithmIdentifiers.RSA_USING_SHA256))
        .build();

    JwtClaims jwtClaims = jwtConsumer.processToClaims(token.getJwtToken());

    val claims = jwtClaims.getClaimsMap();
    assertEquals(claims.get("sub"), responseUser.getId().toString());
    assertEquals(claims.get("username"), responseUser.getUsername());
    assertEquals(claims.get("email"), responseUser.getEmail());
    assertEquals(claims.get("name"), responseUser.getName());
    assertEquals(claims.get("surname"), responseUser.getSurname());
    assertEquals(claims.get("visibility"), responseUser.getVisibility().toString());
  }

  @Test
  public void refreshInvalidToken() throws JoseException, InvalidJwtException {
    val invalidKey = RsaJwkGenerator.generateJwk(2048).getRsaPrivateKey();
    val issuedToken = responseUsers.keySet().iterator().next();

    val jwtConsumer = new JwtConsumerBuilder()
        .setRequireExpirationTime()
        .setAllowedClockSkewInSeconds(30)
        .setRequireSubject()
        .setVerificationKey(rsaJsonWebKey.getKey())
        .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
            AlgorithmIdentifiers.RSA_USING_SHA256))
        .build();

    JwtClaims jwtClaims = jwtConsumer.processToClaims(issuedToken.getJwtToken());

    JsonWebSignature jws = new JsonWebSignature();
    jws.setPayload(jwtClaims.toJson());
    jws.setKey(invalidKey);
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

    assertThrows(RuntimeException.class, () ->
        new RefreshToken(userDAO).execute(jws.getCompactSerialization()));
  }

}
