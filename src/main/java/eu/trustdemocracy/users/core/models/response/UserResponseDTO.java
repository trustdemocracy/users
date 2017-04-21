package eu.trustdemocracy.users.core.models.response;

import java.util.UUID;

public class UserResponseDTO {
    private UUID id;
    private String username;
    private String email;

    public UUID getId() {
        return id;
    }

    public UserResponseDTO setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserResponseDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserResponseDTO setEmail(String email) {
        this.email = email;
        return this;
    }
}
