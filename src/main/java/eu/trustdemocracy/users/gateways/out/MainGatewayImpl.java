package eu.trustdemocracy.users.gateways.out;

import eu.trustdemocracy.users.core.entities.User;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import lombok.val;

public class MainGatewayImpl implements MainGateway {

  private Vertx vertx = Vertx.vertx();
  private WebClient webClient;
  private static String rankerHost;
  private static String votesHost;
  private static String socialHost;
  private static Integer rankerPort;
  private static Integer votesPort;
  private static Integer socialPort;

  @Override
  public void addUser(User user) {
    val json = new JsonObject()
        .put("id", user.getId().toString());

    getWebClient()
        .post(getRankerPort(), getRankerHost(), "/users")
        .rxSendJson(json)
        .subscribe();

    getWebClient()
        .post(getVotesPort(), getVotesHost(), "/users")
        .rxSendJson(json)
        .subscribe();

    getWebClient()
        .post(getSocialPort(), getSocialHost(), "/users")
        .rxSendJson(json)
        .subscribe();
  }

  @Override
  public void deleteUser(User user) {
    getWebClient()
        .delete(getRankerPort(), getRankerHost(), "/users/" + user.getId())
        .rxSend()
        .subscribe();

    getWebClient()
        .delete(getVotesPort(), getVotesHost(), "/users/" + user.getId())
        .rxSend()
        .subscribe();

    getWebClient()
        .delete(getSocialPort(), getSocialHost(), "/users/" + user.getId())
        .rxSend()
        .subscribe();
  }

  private WebClient getWebClient() {
    if (webClient == null) {
      webClient = WebClient.create(vertx);
    }
    return webClient;
  }

  private static String getRankerHost() {
    if (rankerHost == null) {
      rankerHost = System.getenv("ranker_host");
    }
    return rankerHost;
  }

  private static int getRankerPort() {
    if (rankerPort == null) {
      rankerPort = Integer.valueOf(System.getenv("ranker_port"));
    }
    return rankerPort;
  }

  private static String getVotesHost() {
    if (votesHost == null) {
      votesHost = System.getenv("votes_host");
    }
    return votesHost;
  }

  private static int getVotesPort() {
    if (votesPort == null) {
      votesPort = Integer.valueOf(System.getenv("votes_port"));
    }
    return votesPort;
  }

  private static String getSocialHost() {
    if (socialHost == null) {
      socialHost = System.getenv("social_host");
    }
    return socialHost;
  }

  private static int getSocialPort() {
    if (socialPort == null) {
      socialPort = Integer.valueOf(System.getenv("social_port"));
    }
    return socialPort;
  }
}
