package eu.trustdemocracy.users.endpoints.controllers;

import eu.trustdemocracy.users.core.entities.utils.UserMapper;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.UUID;
import lombok.val;

public class UserController extends Controller {

  public UserController(Router router) {
    super(router);
  }

  @Override
  public void buildRoutes() {
    router.get("/").handler(this::handleProposals);
    router.post("/users").handler(this::createUser);
  }

  private void handleProposals(RoutingContext routingContext) {
    routingContext.response().putHeader("content-type", "application/json").end("{'status': 'ok'}");
  }

  private void createUser(RoutingContext routingContext) {
    val requestUser = Json.decodeValue(routingContext.getBodyAsString(), UserRequestDTO.class);
    val user = UserMapper.createEntity(requestUser).setId(UUID.randomUUID());

    routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(201)
        .end(Json.encodePrettily(UserMapper.createResponse(user)));
  }
}
