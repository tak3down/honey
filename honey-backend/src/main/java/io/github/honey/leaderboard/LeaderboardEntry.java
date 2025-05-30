package io.github.honey.leaderboard;

import java.time.LocalDateTime;

public final class LeaderboardEntry {
  private String username;
  private int score;
  private long timeElapsed;
  private LocalDateTime completedAt;

  public LeaderboardEntry() {}

  public LeaderboardEntry(
      final String username,
      final int score,
      final long timeElapsed,
      final LocalDateTime completedAt) {
    this.username = username;
    this.score = score;
    this.timeElapsed = timeElapsed;
    this.completedAt = completedAt;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public int getScore() {
    return score;
  }

  public void setScore(final int score) {
    this.score = score;
  }

  public long getTimeElapsed() {
    return timeElapsed;
  }

  public void setTimeElapsed(final long timeElapsed) {
    this.timeElapsed = timeElapsed;
  }

  public LocalDateTime getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(final LocalDateTime completedAt) {
    this.completedAt = completedAt;
  }
}
