package eu.trustdemocracy.users.endpoints.controllers;

import eu.trustdemocracy.users.core.interactors.exceptions.CredentialsNotFoundException;
import eu.trustdemocracy.users.core.models.request.RefreshTokenRequestDTO;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.endpoints.APIMessages;
import eu.trustdemocracy.users.endpoints.App;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
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
    getRouter().post("/token/refresh").handler(this::refreshToken);
  }

  private void getToken(RoutingContext routingContext) {
    val requestUser = Json.decodeValue(routingContext.getBodyAsString(), UserRequestDTO.class);
    val interactor = getInteractorFactory().createGetTokenInteractor();

    try {
      val tokenResponse = interactor.execute(requestUser);
      serveResponse(routingContext, 200, Json.encodePrettily(tokenResponse));
    } catch (CredentialsNotFoundException e) {
      val json = new JsonObject()
          .put("message", APIMessages.BAD_CREDENTIALS);
      serveResponse(routingContext, 401, Json.encodePrettily(json));
    }
  }

  private void refreshToken(RoutingContext routingContext) {
    val accessToken = getAuthorizationToken(routingContext.request());
    val requestDTO = Json
        .decodeValue(routingContext.getBodyAsString(), RefreshTokenRequestDTO.class);
    requestDTO.setAccessToken(accessToken);

    val interactor = getInteractorFactory().createRefreshTokenInteractor();

    try {
      val tokenResponse = interactor.execute(requestDTO);;
      serveResponse(routingContext, 200, Json.encodePrettily(tokenResponse));
    } catch (CredentialsNotFoundException e) {
      val json = new JsonObject()
          .put("message", APIMessages.BAD_CREDENTIALS);
      serveResponse(routingContext, 401, Json.encodePrettily(json));
    }
  }

  private void serveResponse(RoutingContext context, int statusCode, String response) {
    context.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(statusCode)
        .end(response);
  }

  private String getAuthorizationToken(HttpServerRequest request) {
    val header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null) {
      return "";
    }

    val parts = header.split(" ");
    return parts.length == 2 ? parts[1] : "";
  }
}
