package eu.trustdemocracy.users.core.interactors.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.trustdemocracy.users.core.interactors.exceptions.CredentialsNotFoundException;
import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.GetTokenResponseDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.gateways.TokenRepository;
import eu.trustdemocracy.users.gateways.UserRepository;
import eu.trustdemocracy.users.gateways.fake.FakeTokenRepository;
import eu.trustdemocracy.users.gateways.fake.FakeUserRepository;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.val;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetTokenTest {

  private static Map<UUID, UserResponseDTO> responseUsers;
  private static Map<String, UserRequestDTO> inputUsers;
  private UserRepository userRepository;
  private TokenRepository tokenRepository;
  private RsaJsonWebKey rsaJsonWebKey;

  @BeforeEach
  public void init() throws JoseException {
    rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
    JWTKeyFactory.setPrivateKey(rsaJsonWebKey.getPrivateKey());

    userRepository = new FakeUserRepository();
    tokenRepository = new FakeTokenRepository();
    responseUsers = new HashMap<>();
    inputUsers = new HashMap<>();

    val interactor = new CreateUser(userRepository);
    for (int i = 0; i < 10; i++) {
      val inputUser = new UserRequestDTO()
          .setUsername("user" + i)
          .setEmail("user" + i + "@user.com")
          .setPassword("test" + i)
          .setName("Name" + i);

      inputUsers.put(inputUser.getUsername(), inputUser);

      val responseUser = interactor.execute(inputUser);
      responseUsers.put(responseUser.getId(), responseUser);
    }
  }

  @Test
  public void getSingleToken() throws JoseException, InvalidJwtException {
    val responseUser = responseUsers.values().iterator().next();
    val inputUser = inputUsers.get(responseUser.getUsername());

    GetTokenResponseDTO token = new GetToken(userRepository, tokenRepository).execute(inputUser);

    assertNotNull(token.getRefreshToken());

    val jwtConsumer = new JwtConsumerBuilder()
        .setRequireExpirationTime()
        .setAllowedClockSkewInSeconds(30)
        .setRequireSubject()
        .setVerificationKey(rsaJsonWebKey.getKey())
        .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
            AlgorithmIdentifiers.RSA_USING_SHA256))
        .build();

    JwtClaims jwtClaims = jwtConsumer.processToClaims(token.getAccessToken());

    val claims = jwtClaims.getClaimsMap();
    assertEquals(claims.get("sub"), responseUser.getId().toString());
    assertEquals(claims.get("username"), responseUser.getUsername());
    assertEquals(claims.get("email"), responseUser.getEmail());
    assertEquals(claims.get("name"), responseUser.getName());
    assertEquals(claims.get("surname"), responseUser.getSurname());
    assertEquals(claims.get("visibility"), responseUser.getVisibility().toString());
  }


  @Test
  public void getTokenForNonExistingUser() throws JoseException, InvalidJwtException {
    val inputUser = new UserRequestDTO()
        .setUsername("nonexistinguser")
        .setPassword("test");

    assertThrows(CredentialsNotFoundException.class,
        () -> new GetToken(userRepository, tokenRepository).execute(inputUser));
  }

}
