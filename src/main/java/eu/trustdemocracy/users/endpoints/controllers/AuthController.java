package eu.trustdemocracy.users.endpoints.controllers;

import eu.trustdemocracy.users.core.interactors.exceptions.CredentialsNotFoundException;
import eu.trustdemocracy.users.core.models.request.RefreshTokenRequestDTO;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.endpoints.App;
import io.vertx.core.json.Json;
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
      serveJsonResponse(routingContext, 200, Json.encodePrettily(tokenResponse));
    } catch (CredentialsNotFoundException e) {
      serveBadCredentials(routingContext);
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
      serveJsonResponse(routingContext, 200, Json.encodePrettily(tokenResponse));
    } catch (CredentialsNotFoundException e) {
      serveBadCredentials(routingContext);
    }
  }

}
