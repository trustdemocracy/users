package eu.trustdemocracy.users.gateways.fake;

import eu.trustdemocracy.users.core.entities.User;
import eu.trustdemocracy.users.gateways.UserDAO;

import java.util.UUID;

public class FakeUserDAO implements UserDAO {
    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public UUID getUniqueUUID() {
        return null;
    }
}
