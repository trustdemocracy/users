package eu.trustdemocracy.users.endpoints.controllers;

import io.vertx.ext.web.Router;

public abstract class Controller {
  protected Router router;

  public Controller(Router router) {
    this.router = router;
    buildRoutes();
  }

  public abstract void buildRoutes();
}
