package io.github.honey;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class GameStartRequest {

  private String username;

  @JsonCreator
  public GameStartRequest() {}

  public GameStartRequest(final String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }
}
