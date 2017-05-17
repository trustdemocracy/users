package eu.trustdemocracy.users.endpoints;


import eu.trustdemocracy.users.core.entities.util.CryptoUtils;
import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.models.request.RefreshTokenRequestDTO;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.GetTokenResponseDTO;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import lombok.val;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class AuthControllerTest extends ControllerTest {

  @Test
  public void getToken(TestContext context) {
    val async = context.async();

    val inputUser = new UserRequestDTO()
        .setUsername("test")
        .setEmail("test@test.com")
        .setPassword("password")
        .setName("TestName")
        .setSurname("TestSurname");
    val interactor = interactorFactory.createUserInteractor(CreateUser.class);
    val responseUser = interactor.execute(inputUser);

    val jsonUser = new JsonObject()
        .put("username", inputUser.getUsername())
        .put("password", inputUser.getPassword());

    val single = client.post(port, HOST, "/token")
        .rxSendJson(jsonUser);

    single.subscribe(response -> {
      context.assertEquals(response.statusCode(), 200);
      context.assertTrue(response.headers().get("content-type").contains("application/json"));

      val tokenResponse = Json
          .decodeValue(response.body().toString(), GetTokenResponseDTO.class);

      context.assertNotNull(tokenResponse.getAccessToken());
      context.assertNotNull(tokenResponse.getRefreshToken());

      val jwtConsumer = new JwtConsumerBuilder()
          .setRequireExpirationTime()
          .setAllowedClockSkewInSeconds(30)
          .setRequireSubject()
          .setVerificationKey(JWTKeyFactory.getPublicKey())
          .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
              AlgorithmIdentifiers.RSA_USING_SHA256))
          .build();

      try {
        val jwtClaims = jwtConsumer.processToClaims(tokenResponse.getAccessToken());
        val claims = jwtClaims.getClaimsMap();
        context.assertEquals(claims.get("sub"), responseUser.getId().toString());
        context.assertEquals(claims.get("username"), responseUser.getUsername());
        context.assertEquals(claims.get("email"), responseUser.getEmail());
        context.assertEquals(claims.get("name"), responseUser.getName());
        context.assertEquals(claims.get("surname"), responseUser.getSurname());
        context.assertEquals(claims.get("visibility"), responseUser.getVisibility().toString());
      } catch (InvalidJwtException e) {
        context.fail(e);
      } finally {
        async.complete();
      }
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

  @Test
  public void getTokenFromNonExistingUser(TestContext context) {
    val async = context.async();

    val jsonUser = new JsonObject()
        .put("username", "test")
        .put("password", "password");

    val single = client.post(port, HOST, "/token")
        .rxSendJson(jsonUser);

    assertBadCredentials(context, async, single);
  }

  @Test
  public void refreshToken(TestContext context) {
    val async = context.async();

    val inputUser = new UserRequestDTO()
        .setUsername("test")
        .setEmail("test@test.com")
        .setPassword("password")
        .setName("TestName")
        .setSurname("TestSurname");
    val userInteractor = interactorFactory.createUserInteractor(CreateUser.class);
    val responseUser = userInteractor.execute(inputUser);

    val authInteractor = interactorFactory.createGetTokenInteractor();
    val getTokenResponse = authInteractor.execute(inputUser);

    val tokenRequest = new RefreshTokenRequestDTO()
        .setRefreshToken(getTokenResponse.getRefreshToken());

    val single = client.post(port, HOST, "/token/refresh")
        .putHeader("Authorization", "Bearer " + getTokenResponse.getAccessToken())
        .rxSendJson(tokenRequest);

    single.subscribe(response -> {
      context.assertEquals(response.statusCode(), 200);
      context.assertTrue(response.headers().get("content-type").contains("application/json"));

      val tokenResponse = Json
          .decodeValue(response.body().toString(), GetTokenResponseDTO.class);

      context.assertNotNull(tokenResponse.getAccessToken());
      context.assertNotNull(tokenResponse.getRefreshToken());

      context.assertNotEquals(getTokenResponse.getAccessToken(), tokenResponse.getAccessToken());
      context.assertNotEquals(getTokenResponse.getRefreshToken(), tokenResponse.getAccessToken());

      val jwtConsumer = new JwtConsumerBuilder()
          .setRequireExpirationTime()
          .setAllowedClockSkewInSeconds(30)
          .setRequireSubject()
          .setVerificationKey(JWTKeyFactory.getPublicKey())
          .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
              AlgorithmIdentifiers.RSA_USING_SHA256))
          .build();

      try {
        val jwtClaims = jwtConsumer.processToClaims(tokenResponse.getAccessToken());
        val claims = jwtClaims.getClaimsMap();
        context.assertEquals(claims.get("username"), responseUser.getUsername());
        context.assertEquals(claims.get("sub"), responseUser.getId().toString());
        context.assertEquals(claims.get("email"), responseUser.getEmail());
        context.assertEquals(claims.get("name"), responseUser.getName());
        context.assertEquals(claims.get("surname"), responseUser.getSurname());
        context.assertEquals(claims.get("visibility"), responseUser.getVisibility().toString());
      } catch (InvalidJwtException e) {
        context.fail(e);
      } finally {
        async.complete();
      }
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

  @Test
  public void refreshInvalidToken(TestContext context) {
    val async = context.async();

    val inputUser = new UserRequestDTO()
        .setUsername("test")
        .setEmail("test@test.com")
        .setPassword("password")
        .setName("TestName")
        .setSurname("TestSurname");
    val userInteractor = interactorFactory.createUserInteractor(CreateUser.class);
    userInteractor.execute(inputUser);

    val authInteractor = interactorFactory.createGetTokenInteractor();
    val getTokenResponse = authInteractor.execute(inputUser);

    val tokenRequest = new RefreshTokenRequestDTO()
        .setRefreshToken(CryptoUtils.randomToken());

    val single = client.post(port, HOST, "/token/refresh")
        .putHeader("Authorization", "Bearer " + getTokenResponse.getAccessToken())
        .rxSendJson(tokenRequest);

    assertBadCredentials(context, async, single);
  }
}
