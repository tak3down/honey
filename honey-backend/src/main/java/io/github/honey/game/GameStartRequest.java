package io.github.honey.game;

public final class GameStartRequest {
  private String username;

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
