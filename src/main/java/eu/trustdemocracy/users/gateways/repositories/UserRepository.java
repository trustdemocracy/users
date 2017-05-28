package eu.trustdemocracy.users.gateways.repositories;

import eu.trustdemocracy.users.core.entities.User;
import java.util.List;
import java.util.UUID;

public interface UserRepository {

  User create(User user);

  User findByUsername(String username);

  User update(User user);

  User findById(UUID id);

  User deleteById(UUID id);

  List<User> findAll();
}
