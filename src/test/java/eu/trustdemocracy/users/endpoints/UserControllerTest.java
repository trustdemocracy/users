package eu.trustdemocracy.users.endpoints;


import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import eu.trustdemocracy.users.infrastructure.FakeInteractorFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import java.io.IOException;
import java.net.ServerSocket;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class UserControllerTest {

  private static final String HOST = "localhost";

  private Vertx vertx;
  private Integer port;
  private WebClient client;

  @Before
  public void setUp(TestContext context) throws IOException {
    vertx = Vertx.vertx();
    client = WebClient.create(vertx);

    val socket = new ServerSocket(0);
    port = socket.getLocalPort();
    socket.close();

    val options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

    App.setInteractorFactory(new FakeInteractorFactory());
    vertx.deployVerticle(App.class.getName(), options, context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testMyApplication(TestContext context) {
    val async = context.async();

    client.get(port, HOST, "/")
        .rxSend()
        .subscribe(response -> {
          context.assertTrue(response.body().toString().contains("status"));
          async.complete();
        });
  }

  @Test
  public void createUser(TestContext context) {
    val async = context.async();

    val userRequest = new UserRequestDTO()
        .setUsername("test")
        .setEmail("test@test.com")
        .setPassword("password")
        .setName("TestName")
        .setSurname("TestSurname");

    val single = client.post(port, HOST, "/users")
        .rxSendJson(userRequest);

    single.subscribe(response -> {
      context.assertEquals(response.statusCode(), 201);
      context.assertTrue(response.headers().get("content-type").contains("application/json"));

      val responseUser = Json.decodeValue(response.body().toString(), UserResponseDTO.class);
      context.assertEquals(userRequest.getUsername(), responseUser.getUsername());
      context.assertEquals(userRequest.getEmail(), responseUser.getEmail());
      context.assertEquals(userRequest.getName(), responseUser.getName());
      context.assertEquals(userRequest.getSurname(), responseUser.getSurname());
      context.assertNotNull(responseUser.getId());

      async.complete();
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

  @Test
  public void createAndFindUser(TestContext context) {
    val async = context.async();

    val userRequest = new UserRequestDTO()
        .setUsername("test")
        .setEmail("test@test.com")
        .setPassword("password")
        .setName("TestName")
        .setSurname("TestSurname");

    val single = client.post(port, HOST, "/users")
        .rxSendJson(userRequest);

    single.subscribe(response -> {
      val responseUser = Json.decodeValue(response.body().toString(), UserResponseDTO.class);

      client.get(port, HOST, "/users/" + responseUser.getId())
          .rxSend()

          .subscribe(getResponse -> {
            context.assertEquals(getResponse.statusCode(), 200);
            context
                .assertTrue(getResponse.headers().get("content-type").contains("application/json"));

            val newResponseUser = Json
                .decodeValue(getResponse.body().toString(), UserResponseDTO.class);
            context.assertEquals(responseUser.getId(), newResponseUser.getId());
            context
                .assertEquals(responseUser.getUsername(), newResponseUser.getUsername());
            context.assertEquals(responseUser.getEmail(), newResponseUser.getEmail());
            context.assertEquals(responseUser.getName(), newResponseUser.getName());
            context.assertEquals(responseUser.getSurname(), newResponseUser.getSurname());

            async.complete();
          }, error -> {
            context.fail(error);
            async.complete();
          });

    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

}
