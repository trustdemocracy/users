package eu.trustdemocracy.users.endpoints.controllers;

import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.interactors.user.GetUser;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.endpoints.App;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import java.util.UUID;
import lombok.val;

public class UserController extends Controller {

  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

  public UserController(App app) {
    super(app);
  }

  @Override
  public void buildRoutes() {
    getRouter().get("/").handler(this::handleProposals);
    getRouter().post("/users").handler(this::createUser);
    getRouter().get("/users/:id").handler(this::findUser);
  }

  private void handleProposals(RoutingContext routingContext) {
    routingContext.response().putHeader("content-type", "application/json").end("{'status': 'ok'}");
  }

  private void createUser(RoutingContext routingContext) {
    val requestUser = Json.decodeValue(routingContext.getBodyAsString(), UserRequestDTO.class);
    val interactor = getInteractorFactory().createUserInteractor(CreateUser.class);
    val user = interactor.execute(requestUser);

    routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(201)
        .end(Json.encodePrettily(user));
  }

  private void findUser(RoutingContext routingContext) {
    val id = UUID.fromString(routingContext.pathParam("id"));
    val requestUser = new UserRequestDTO().setId(id);
    val interactor = getInteractorFactory().createUserInteractor(GetUser.class);
    val user = interactor.execute(requestUser);
    LOG.info(user);

    routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(200)
        .end(Json.encodePrettily(user));
  }
}
