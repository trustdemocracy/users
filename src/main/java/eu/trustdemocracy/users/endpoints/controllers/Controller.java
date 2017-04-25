package eu.trustdemocracy.users.endpoints.controllers;

import eu.trustdemocracy.users.endpoints.App;
import eu.trustdemocracy.users.infrastructure.InteractorFactory;
import io.vertx.ext.web.Router;

public abstract class Controller {
  private App app;

  public Controller(App app) {
    this.app = app;
    buildRoutes();
  }

  protected Router getRouter() {
    return app.getRouter();
  }

  protected InteractorFactory getInteractorFactory() {
    return app.getInteractorFactory();
  }

  public abstract void buildRoutes();
}
