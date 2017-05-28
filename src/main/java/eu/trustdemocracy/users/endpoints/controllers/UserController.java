package eu.trustdemocracy.users.endpoints.controllers;

import eu.trustdemocracy.users.core.interactors.exceptions.InvalidTokenException;
import eu.trustdemocracy.users.core.interactors.exceptions.UserNotFoundException;
import eu.trustdemocracy.users.core.interactors.exceptions.UsernameAlreadyExistsException;
import eu.trustdemocracy.users.core.models.request.RankRequestDTO;
import eu.trustdemocracy.users.core.models.request.UserRequestDTO;
import eu.trustdemocracy.users.endpoints.APIMessages;
import eu.trustdemocracy.users.endpoints.App;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.UUID;
import lombok.val;

public class UserController extends Controller {

  public UserController(App app) {
    super(app);
  }

  @Override
  public void buildRoutes() {
    getRouter().get("/users").handler(this::findAll);
    getRouter().post("/users").handler(this::createUser);
    getRouter().get("/users/:id").handler(this::findUser);
    getRouter().put("/users/:id").handler(this::updateUser);
    getRouter().delete("/users/:id").handler(this::deleteUser);
    getRouter().post("/rank").handler(this::updateRank);
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

    val interactor = getInteractorFactory().getCreateUser();

    try {
      val user = interactor.execute(requestUser);
      serveJsonResponse(routingContext, 201, Json.encodePrettily(user));
    } catch (UsernameAlreadyExistsException e) {
      val json = new JsonObject()
          .put("message", APIMessages.EXISTING_USERNAME);
      serveJsonResponse(routingContext, 400, Json.encodePrettily(json));
    }
  }

  private void findUser(RoutingContext routingContext) {
    val requestUser = new UserRequestDTO();
    try {
      val id = routingContext.pathParam("id");
      if (id == null || id.isEmpty()) {
        throw new Exception();
      }

      try {
        requestUser.setId(UUID.fromString(id));
      } catch (IllegalArgumentException e) {
        requestUser.setUsername(id);
      }
    } catch (Exception e) {
      serveBadRequest(routingContext);
      return;
    }

    val interactor = getInteractorFactory().getGetUser();
    try {
      val user = interactor.execute(requestUser);
      serveJsonResponse(routingContext, 200, Json.encodePrettily(user));
    } catch (UserNotFoundException e) {
      serveNotFound(routingContext);
    }

  }

  private void findAll(RoutingContext routingContext) {
    val accessToken = getAuthorizationToken(routingContext.request());
    val requestUser = new UserRequestDTO()
        .setAccessToken(accessToken);

    val interactor = getInteractorFactory().getGetUsers();
    val users = interactor.execute(requestUser);

    serveJsonResponse(routingContext, 200, Json.encodePrettily(users));
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
    val interactor = getInteractorFactory().getUpdateUser();

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
    val interactor = getInteractorFactory().getDeleteUser();

    try {
      val user = interactor.execute(requestUser);
      serveJsonResponse(routingContext, 200, Json.encodePrettily(user));
    } catch (InvalidTokenException e) {
      serveBadCredentials(routingContext);
    }
  }

  private void updateRank(RoutingContext context) {
    try {
      val request = Json.decodeValue(context.getBodyAsString(), RankRequestDTO.class);
      val interactor = getInteractorFactory().getUpdateRank();
      interactor.execute(request);
    } catch (Exception e) {
      serveBadRequest(context);
    }
  }
}
