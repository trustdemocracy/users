package eu.trustdemocracy.users.endpoints;

import eu.trustdemocracy.users.core.interactors.utils.TokenUtils;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.infrastructure.FakeInteractorFactory;
import eu.trustdemocracy.users.infrastructure.InteractorFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import java.io.IOException;
import java.net.ServerSocket;
import lombok.val;
import org.jose4j.lang.JoseException;
import org.junit.After;
import org.junit.Before;
import rx.Single;

public class ControllerTest {

  protected static final String HOST = "localhost";

  protected Vertx vertx;
  protected Integer port;
  protected WebClient client;
  protected InteractorFactory interactorFactory;

  @Before
  public void setUp(TestContext context) throws IOException, JoseException, InterruptedException {
    TokenUtils.generateKeys();

    vertx = Vertx.vertx();
    client = WebClient.create(vertx);

    val socket = new ServerSocket(0);
    port = socket.getLocalPort();
    socket.close();

    val options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

    interactorFactory = new FakeInteractorFactory();
    App.setInteractorFactory(interactorFactory);
    vertx.deployVerticle(App.class.getName(), options, context.asyncAssertSuccess());

    Thread.sleep(200);
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  protected void assertBadCredentials(TestContext context, Async async,
      Single<HttpResponse<Buffer>> single) {
    single.subscribe(response -> {
      context.assertEquals(response.statusCode(), 401);
      context.assertTrue(response.headers().get("content-type").contains("application/json"));

      val errorMessage = response.body().toJsonObject().getString("message");

      context.assertEquals(errorMessage, APIMessages.BAD_CREDENTIALS);

      async.complete();
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }


  protected void assertBadRequest(TestContext context, Async async,
      Single<HttpResponse<Buffer>> single) {
    single.subscribe(response -> {
      context.assertEquals(response.statusCode(), 400);
      context.assertTrue(response.headers().get("content-type").contains("application/json"));

      val errorMessage = response.body().toJsonObject().getString("message");

      context.assertEquals(errorMessage, APIMessages.BAD_REQUEST);

      async.complete();
    }, error -> {
      context.fail(error);
      async.complete();
    });
  }

  protected String getRandomToken() {
    val userRequest = new UserRequestDTO()
        .setUsername("test")
        .setEmail("test@test.com")
        .setPassword("password")
        .setName("TestName")
        .setSurname("TestSurname");

    val createUser = interactorFactory.getCreateUser();
    createUser.execute(userRequest);
    val authInteractor = interactorFactory.getGetToken();
    val getTokenResponse = authInteractor.execute(userRequest);

    return "Bearer " + getTokenResponse.getAccessToken();
  }

}
