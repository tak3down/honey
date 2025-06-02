package io.github.honey;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class UserDetails {

  private String username;
  private String password;

  @JsonCreator
  public UserDetails() {}

  public UserDetails(final String username, final String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
