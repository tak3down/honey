package io.github.honey;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class AuthRequest {

  private String username;
  private String password;

  @JsonCreator
  public AuthRequest() {}

  public AuthRequest(final String username, final String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String toString() {
    return "AuthRequest{" + "username='" + username + '\'' + ", password='" + password + '\'' + '}';
  }
}
