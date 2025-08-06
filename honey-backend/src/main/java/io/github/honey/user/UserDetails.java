package io.github.honey.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public final class UserDetails {

  @Id private String username;
  private String password;

  public UserDetails() {}

  public UserDetails(final String username, final String password) {
    this.username = username;
    this.password = password;
  }

  public String username() {
    return username;
  }

  public String password() {
    return password;
  }
}
