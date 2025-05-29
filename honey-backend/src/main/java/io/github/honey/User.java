package io.github.honey;

public class User {
  private String username;
  private String password;
  private String currentSessionId;

  public User() {}

  public User(final String username, final String password) {
    this.username = username;
    this.password = password;
  }

  // Getters and setters
  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getCurrentSessionId() {
    return currentSessionId;
  }

  public void setCurrentSessionId(final String currentSessionId) {
    this.currentSessionId = currentSessionId;
  }
}
