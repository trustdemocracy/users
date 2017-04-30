package eu.trustdemocracy.users.endpoints.controllers;

import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.endpoints.App;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.val;

public class AuthController extends Controller {

  public AuthController(App app) {
    super(app);
  }

  @Override
  public void buildRoutes() {
    getRouter().post("/token").handler(this::getToken);
  }

  private void getToken(RoutingContext routingContext) {
    val requestUser = Json.decodeValue(routingContext.getBodyAsString(), UserRequestDTO.class);
    val interactor = getInteractorFactory().createAuthInteractor(GetToken.class);
    val token = interactor.execute(requestUser);
    val json = new JsonObject().put("token", token);

    routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(200)
        .end(Json.encodePrettily(json));
  }
}
