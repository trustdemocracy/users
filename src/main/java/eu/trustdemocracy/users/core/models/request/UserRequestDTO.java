package eu.trustdemocracy.users.core.models.request;

import java.util.UUID;

public class UserRequestDTO {
    private UUID id;
    private String username;
    private String email;
    private String password;

    public UUID getId() {
        return id;
    }

    public UserRequestDTO setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserRequestDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRequestDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserRequestDTO setPassword(String password) {
        this.password = password;
        return this;
    }
}
