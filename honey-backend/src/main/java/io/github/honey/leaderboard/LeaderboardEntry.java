package io.github.honey.leaderboard;

import java.time.LocalDateTime;
import java.util.Objects;

public final class LeaderboardEntry implements Comparable<LeaderboardEntry> {
  private String username;
  private int score;
  private long timeElapsed;
  private LocalDateTime completedAt;

  public LeaderboardEntry() {}

  public LeaderboardEntry(String username, int score, long timeElapsed, LocalDateTime completedAt) {
    this.username = username;
    this.score = score;
    this.timeElapsed = timeElapsed;
    this.completedAt = completedAt;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public long getTimeElapsed() {
    return timeElapsed;
  }

  public void setTimeElapsed(long timeElapsed) {
    this.timeElapsed = timeElapsed;
  }

  public LocalDateTime getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(LocalDateTime completedAt) {
    this.completedAt = completedAt;
  }

  @Override
  public int compareTo(LeaderboardEntry obj) {
    if (obj == null) return 1;
    if (score == obj.score) return completedAt.compareTo(obj.completedAt);

    return score - obj.score;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) return false;
    LeaderboardEntry that = (LeaderboardEntry) obj;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(username);
  }
}
