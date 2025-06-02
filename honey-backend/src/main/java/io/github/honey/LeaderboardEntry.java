package io.github.honey;

import java.time.LocalDateTime;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class LeaderboardEntry implements Comparable<LeaderboardEntry> {
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

  @Override
  public int compareTo(@NotNull final LeaderboardEntry obj) {
    if (obj.getScore() == getScore()) {

      if (obj.getTimeElapsed() == getTimeElapsed()) {
        return getCompletedAt().compareTo(obj.getCompletedAt());
      }

      return obj.getTimeElapsed() > getTimeElapsed() ? -1 : 1;
    }

    return Integer.compare(obj.getScore(), getScore());
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final LeaderboardEntry that = (LeaderboardEntry) obj;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(username);
  }
}
