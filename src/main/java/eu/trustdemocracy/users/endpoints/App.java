package eu.trustdemocracy.users.endpoints;

import eu.trustdemocracy.users.endpoints.controllers.Controller;
import eu.trustdemocracy.users.endpoints.controllers.UserController;
import eu.trustdemocracy.users.endpoints.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;

public class App extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);
  private static final int DEFAULT_PORT = 8080;

  public static void main(String... args) {
    Runner.runVerticle(App.class.getName());
  }

  @Override
  public void start() {
    val port = config().getInteger("http.port", DEFAULT_PORT);

    vertx.executeBlocking(future -> {
      Router router = Router.router(vertx);
      router.route().handler(BodyHandler.create());
      registerControllers(router);

      vertx.createHttpServer()
          .requestHandler(router::accept)
          .listen(port);

      future.complete();
    }, result -> {
      if (result.succeeded()) {
        LOG.info("App listening on port: " + port);
      } else {
        LOG.error("Failed to start verticle", result.cause());
      }
    });
  }

  private void registerControllers(Router router) {
    val controllers = Stream.of(
        UserController.class
    ).collect(Collectors.toCollection(HashSet<Class<? extends Controller>>::new));

    for (val controller : controllers) {
      try {
        val constructor = controller.getConstructor(Router.class);
        constructor.newInstance(router);
      } catch (Exception e) {
        LOG.error("Failing to attach controller [" + controller.getName() + "]", e);
      }
    }
  }

}
