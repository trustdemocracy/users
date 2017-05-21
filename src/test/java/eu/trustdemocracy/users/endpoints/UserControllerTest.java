package eu.trustdemocracy.users.endpoints;


import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.core.models.response.UserResponseDTO;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import java.util.UUID;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class UserControllerTest extends ControllerTest {

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
  public void createExistingUser(TestContext context) {
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

      single.subscribe(repeteadedResponse -> {
        context.assertEquals(repeteadedResponse.statusCode(), 400);

        val errorMessage = repeteadedResponse.body().toJsonObject().getString("message");
        context.assertEquals(errorMessage, APIMessages.EXISTING_USERNAME);

        async.complete();
      });
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

  @Test
  public void createUpdateAndFindUser(TestContext context) {
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

      val authInteractor = interactorFactory.getGetToken();
      val getTokenResponse = authInteractor.execute(userRequest);

      userRequest.setId(responseUser.getId())
          .setEmail("newEmail")
          .setPassword("newPass")
          .setName("NewName")
          .setSurname("NewName");

      client.put(port, HOST, "/users/" + userRequest.getId())
          .putHeader("Authorization", "Bearer " + getTokenResponse.getAccessToken())
          .rxSendJson(userRequest)
          .subscribe(putResponse -> {
            context.assertEquals(putResponse.statusCode(), 200);

            client.get(port, HOST, "/users/" + responseUser.getId())
                .rxSend()
                .subscribe(getResponse -> {

                  val newResponseUser = Json
                      .decodeValue(getResponse.body().toString(), UserResponseDTO.class);
                  context.assertEquals(userRequest.getId(), newResponseUser.getId());
                  context
                      .assertEquals(userRequest.getUsername(), newResponseUser.getUsername());
                  context.assertEquals(userRequest.getEmail(), newResponseUser.getEmail());
                  context.assertEquals(userRequest.getName(), newResponseUser.getName());
                  context.assertEquals(userRequest.getSurname(), newResponseUser.getSurname());

                  async.complete();
                }, error -> {
                  context.fail(error);
                  async.complete();
                });
          }, error -> {
            context.fail(error);
            async.complete();
          });
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

  @Test
  public void createAndUpdateNotAuthorized(TestContext context) {
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

      userRequest.setId(responseUser.getId())
          .setEmail("newEmail")
          .setPassword("newPass")
          .setName("NewName")
          .setSurname("NewName");

      val updateSingle = client.put(port, HOST, "/users/" + userRequest.getId())
          .rxSendJson(userRequest);

      assertBadCredentials(context, async, updateSingle);
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

  @Test
  public void createDeleteAndFindUser(TestContext context) {
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

      val authInteractor = interactorFactory.getGetToken();
      val getTokenResponse = authInteractor.execute(userRequest);

      client.delete(port, HOST, "/users/" + responseUser.getId())
          .putHeader("Authorization", "Bearer " + getTokenResponse.getAccessToken())
          .rxSend()
          .subscribe(deleteResponse -> {
            context.assertEquals(deleteResponse.statusCode(), 200);

            client.get(port, HOST, "/users/" + responseUser.getId())
                .rxSend()
                .subscribe(getResponse -> {

                  val newResponseUser = Json
                      .decodeValue(getResponse.body().toString(), UserResponseDTO.class);
                  context.assertNull(newResponseUser.getId());
                  context.assertNull(newResponseUser.getUsername());
                  context.assertNull(newResponseUser.getEmail());
                  context.assertNull(newResponseUser.getName());
                  context.assertNull(newResponseUser.getSurname());

                  async.complete();
                }, error -> {
                  context.fail(error);
                  async.complete();
                });
          }, error -> {
            context.fail(error);
            async.complete();
          });
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

  @Test
  public void createAndDeleteNotAuthorized(TestContext context) {
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
      val deletionSingle = client.delete(port, HOST, "/users/" + responseUser.getId())
          .rxSend();
      assertBadCredentials(context, async, deletionSingle);
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

  @Test
  public void createUserBadRequest(TestContext context) {
    val async = context.async();

    val single = client.post(port, HOST, "/users")
        .putHeader("Authorization", getRandomToken())
        .rxSendJson(new JsonObject());

    assertBadRequest(context, async, single);
  }

  @Test
  public void findUserByUsername(TestContext context) {
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
      context.assertEquals(201, response.statusCode());

      client.get(port, HOST, "/users/" + userRequest.getUsername())
          .rxSend()
          .subscribe(getResponse -> {
            context.assertEquals(200, getResponse.statusCode());
            val responseUser = Json
                .decodeValue(getResponse.body().toString(), UserResponseDTO.class);
            context.assertNotNull(responseUser.getId());
            async.complete();
          });
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

  @Test
  public void findNonExistingUser(TestContext context) {
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
      context.assertEquals(201, response.statusCode());
      client.get(port, HOST, "/users/notAnId")
          .rxSend()
          .subscribe(getResponse -> {
            context.assertEquals(404, getResponse.statusCode());
            async.complete();
          });
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

  @Test
  public void updateUserBadRequest(TestContext context) {
    val async = context.async();

    val single = client.put(port, HOST, "/users/" + UUID.randomUUID())
        .putHeader("Authorization", getRandomToken())
        .rxSendJson(new JsonObject());

    assertBadRequest(context, async, single);
  }

}
