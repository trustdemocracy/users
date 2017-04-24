package eu.trustdemocracy.users.endpoints;


import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
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

  @Before
  public void setUp(TestContext context) throws IOException {
    vertx = Vertx.vertx();

    val socket = new ServerSocket(0);
    port = socket.getLocalPort();
    socket.close();

    val options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

    vertx.deployVerticle(App.class.getName(), options, context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testMyApplication(TestContext context) {
    val async = context.async();

    vertx.createHttpClient().getNow(port, HOST, "/", response -> {
      response.handler(body -> {
        context.assertTrue(body.toString().contains("status"));
        async.complete();
      });
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

    val jsonUser = Json.encodePrettily(userRequest);
    val length = Integer.toString(jsonUser.length());


    vertx.createHttpClient().post(port, HOST, "/users")
        .putHeader("content-type", "application/json")
        .putHeader("content-length", length)
        .handler(response -> {
          context.assertEquals(response.statusCode(), 201);
          context.assertTrue(response.headers().get("content-type").contains("application/json"));

          response.bodyHandler(body -> {
            try {
              val responseUser = Json.decodeValue(body.toString(), UserResponseDTO.class);
              context.assertEquals(responseUser.getUsername(), userRequest.getUsername());
              context.assertEquals(responseUser.getEmail(), userRequest.getEmail());
              context.assertEquals(responseUser.getName(), userRequest.getName());
              context.assertEquals(responseUser.getSurname(), userRequest.getSurname());
              context.assertNotNull(responseUser.getId());
            } catch (Exception e) {
              context.fail(e);
            } finally {
              async.complete();
            }
          });
        })
        .write(jsonUser)
        .end();
  }
}
