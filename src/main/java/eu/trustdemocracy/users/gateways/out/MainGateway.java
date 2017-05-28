package eu.trustdemocracy.users.gateways.out;

import eu.trustdemocracy.users.core.entities.User;

public interface MainGateway {

  void addUser(User user);

  void deleteUser(User user);
}
