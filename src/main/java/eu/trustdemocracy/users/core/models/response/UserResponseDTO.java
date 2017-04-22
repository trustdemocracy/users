package eu.trustdemocracy.users.core.models.response;

import eu.trustdemocracy.users.core.entities.UserVisibility;
import java.util.UUID;

public class UserResponseDTO {

  private UUID id;
  private String username;
  private String email;
  private String name;
  private String surname;
  private UserVisibility visibility;

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

  public String getName() {
    return name;
  }

  public UserResponseDTO setName(String name) {
    this.name = name;
    return this;
  }

  public String getSurname() {
    return surname;
  }

  public UserResponseDTO setSurname(String surname) {
    this.surname = surname;
    return this;
  }

  public UserResponseDTO setVisibility(UserVisibility visibility) {
    this.visibility = visibility;
    return this;
  }

  public UserVisibility getVisibility() {
    return visibility;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UserResponseDTO that = (UserResponseDTO) o;

    if (id != null ? !id.equals(that.id) : that.id != null) {
      return false;
    }
    if (username != null ? !username.equals(that.username) : that.username != null) {
      return false;
    }
    if (email != null ? !email.equals(that.email) : that.email != null) {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null) {
      return false;
    }
    return surname != null ? surname.equals(that.surname) : that.surname == null;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (username != null ? username.hashCode() : 0);
    result = 31 * result + (email != null ? email.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (surname != null ? surname.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "UserResponseDTO{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", email='" + email + '\'' +
        ", name='" + name + '\'' +
        ", surname='" + surname + '\'' +
        '}';
  }
}
