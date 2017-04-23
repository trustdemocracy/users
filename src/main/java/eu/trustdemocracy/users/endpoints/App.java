package eu.trustdemocracy.users.endpoints;

import eu.trustdemocracy.users.endpoints.controllers.Controller;
import eu.trustdemocracy.users.endpoints.controllers.UserController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
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
  private static final int PORT = 8080;

  public static void main(String... args) {
    val app = new App();
    app.vertx = Vertx.vertx();
    app.start();
  }

  @Override
  public void start() {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    registerControllers(router);

    vertx.createHttpServer()
        .requestHandler(router::accept)
        .listen(PORT);

    LOG.info("App listening on port: " + PORT);
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
