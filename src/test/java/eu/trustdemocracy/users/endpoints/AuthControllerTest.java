package eu.trustdemocracy.users.endpoints;


import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.infrastructure.FakeInteractorFactory;
import eu.trustdemocracy.users.infrastructure.InteractorFactory;
import eu.trustdemocracy.users.infrastructure.JWTKeyFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import java.io.IOException;
import java.net.ServerSocket;
import lombok.val;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class AuthControllerTest {

  private static final String HOST = "localhost";

  private Vertx vertx;
  private Integer port;
  private WebClient client;
  private RsaJsonWebKey rsaJsonWebKey;
  private InteractorFactory interactorFactory;

  @Before
  public void setUp(TestContext context) throws IOException, JoseException {
    rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
    JWTKeyFactory.setPrivateKey(rsaJsonWebKey.getPrivateKey());

    vertx = Vertx.vertx();
    client = WebClient.create(vertx);

    val socket = new ServerSocket(0);
    port = socket.getLocalPort();
    socket.close();

    val options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

    interactorFactory = new FakeInteractorFactory();
    App.setInteractorFactory(interactorFactory);
    vertx.deployVerticle(App.class.getName(), options, context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

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

      val token = response.body().toJsonObject().getString("token");

      val jwtConsumer = new JwtConsumerBuilder()
          .setRequireExpirationTime()
          .setAllowedClockSkewInSeconds(30)
          .setRequireSubject()
          .setVerificationKey(rsaJsonWebKey.getKey())
          .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
              AlgorithmIdentifiers.RSA_USING_SHA256))
          .build();

      try {
        val jwtClaims = jwtConsumer.processToClaims(token);
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


    val authInteractor = interactorFactory.createAuthInteractor(GetToken.class);
    val jsonToken = new JsonObject().put("token", authInteractor.execute(inputUser));

    val single = client.post(port, HOST, "/token/refresh")
        .rxSendJson(jsonToken);

    single.subscribe(response -> {
      context.assertEquals(response.statusCode(), 200);
      context.assertTrue(response.headers().get("content-type").contains("application/json"));

      val token = response.body().toJsonObject().getString("token");

      context.assertNotEquals(jsonToken.getString("token"), token);

      val jwtConsumer = new JwtConsumerBuilder()
          .setRequireExpirationTime()
          .setAllowedClockSkewInSeconds(30)
          .setRequireSubject()
          .setVerificationKey(rsaJsonWebKey.getKey())
          .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
              AlgorithmIdentifiers.RSA_USING_SHA256))
          .build();

      try {
        val jwtClaims = jwtConsumer.processToClaims(token);
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
}
