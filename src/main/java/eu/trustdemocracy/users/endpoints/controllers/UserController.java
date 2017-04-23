package eu.trustdemocracy.users.endpoints.controllers;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class UserController extends Controller {

  public UserController(Router router) {
    super(router);
  }

  @Override
  public void buildRoutes() {
    router.get("/").handler(this::handleProposals);
  }

  private void handleProposals(RoutingContext routingContext) {
    routingContext.response().putHeader("content-type", "application/json").end("{'status': 'ok'}");
  }
}
