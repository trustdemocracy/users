package eu.trustdemocracy.users.endpoints.controllers;

import eu.trustdemocracy.users.core.interactors.exceptions.InvalidTokenException;
import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.interactors.user.DeleteUser;
import eu.trustdemocracy.users.core.interactors.user.GetUser;
import eu.trustdemocracy.users.core.interactors.user.UpdateUser;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.endpoints.App;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import java.util.UUID;
import lombok.val;

public class UserController extends Controller {

  public UserController(App app) {
    super(app);
  }

  @Override
  public void buildRoutes() {
    getRouter().post("/users").handler(this::createUser);
    getRouter().get("/users/:id").handler(this::findUser);
    getRouter().put("/users/:id").handler(this::updateUser);
    getRouter().delete("/users/:id").handler(this::deleteUser);
  }

  private void createUser(RoutingContext routingContext) {
    UserRequestDTO requestUser;
    try {
      if (routingContext.getBodyAsJson().isEmpty()) {
        throw new Exception();
      }

      requestUser = Json.decodeValue(routingContext.getBodyAsString(), UserRequestDTO.class);
    } catch (Exception e) {
      serveBadRequest(routingContext);
      return;
    }

    val interactor = getInteractorFactory().createUserInteractor(CreateUser.class);
    val user = interactor.execute(requestUser);

    serveJsonResponse(routingContext, 201, Json.encodePrettily(user));
  }

  private void findUser(RoutingContext routingContext) {
    UUID id;
    try {
      id = UUID.fromString(routingContext.pathParam("id"));
    } catch (Exception e) {
      serveBadRequest(routingContext);
      return;
    }

    val requestUser = new UserRequestDTO().setId(id);
    val interactor = getInteractorFactory().createUserInteractor(GetUser.class);
    val user = interactor.execute(requestUser);

    serveJsonResponse(routingContext, 200, Json.encodePrettily(user));
  }

  private void updateUser(RoutingContext routingContext) {
    val accessToken = getAuthorizationToken(routingContext.request());
    UserRequestDTO requestUser;
    try {
      if (routingContext.getBodyAsJson().isEmpty()) {
        throw new Exception();
      }

      requestUser = Json.decodeValue(routingContext.getBodyAsString(), UserRequestDTO.class);
    } catch (Exception e) {
      serveBadRequest(routingContext);
      return;
    }
    requestUser.setAccessToken(accessToken);
    val interactor = getInteractorFactory().createUserInteractor(UpdateUser.class);

    try {
      val user = interactor.execute(requestUser);
      serveJsonResponse(routingContext, 200, Json.encodePrettily(user));
    } catch (InvalidTokenException e) {
      serveBadCredentials(routingContext);
    }
  }

  private void deleteUser(RoutingContext routingContext) {
    val accessToken = getAuthorizationToken(routingContext.request());
    val requestUser = new UserRequestDTO()
        .setAccessToken(accessToken);
    val interactor = getInteractorFactory().createUserInteractor(DeleteUser.class);

    try {
      val user = interactor.execute(requestUser);
      serveJsonResponse(routingContext, 200, Json.encodePrettily(user));
    } catch (InvalidTokenException e) {
      serveBadCredentials(routingContext);
    }
  }
}
