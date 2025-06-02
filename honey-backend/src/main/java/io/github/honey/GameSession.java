package io.github.honey;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.LinkedList;
import java.util.List;

public final class GameSession {

  private String sessionId;
  private String username;
  private int score;
  private int totalQuestions;
  private long startTime;
  private long endTime;
  private boolean isFinished;
  private GameQuestion currentQuestion;
  private int questionNumber;
  private List<String> countriesLeft;

  @JsonCreator
  public GameSession() {}

  public GameSession(final String sessionId, final String username) {
    this.sessionId = sessionId;
    this.username = username;
    this.score = 0;
    this.totalQuestions = 10;
    this.startTime = System.currentTimeMillis();
    this.isFinished = false;
    this.questionNumber = 1;
    this.countriesLeft = new LinkedList<>();
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(final String sessionId) {
    this.sessionId = sessionId;
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

  public int getTotalQuestions() {
    return totalQuestions;
  }

  public void setTotalQuestions(final int totalQuestions) {
    this.totalQuestions = totalQuestions;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(final long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(final long endTime) {
    this.endTime = endTime;
  }

  public boolean isFinished() {
    return isFinished;
  }

  public void setFinished(final boolean finished) {
    isFinished = finished;
  }

  public GameQuestion getCurrentQuestion() {
    return currentQuestion;
  }

  public void setCurrentQuestion(final GameQuestion currentQuestion) {
    this.currentQuestion = currentQuestion;
  }

  public int getQuestionNumber() {
    return questionNumber;
  }

  public void setQuestionNumber(final int questionNumber) {
    this.questionNumber = questionNumber;
  }

  public List<String> getCountriesLeft() {
    return countriesLeft;
  }

  public void setCountriesLeft(final List<String> countriesLeft) {
    this.countriesLeft = countriesLeft;
  }

  @Override
  public String toString() {
    return "GameSession{"
        + "sessionId='"
        + sessionId
        + '\''
        + ", username='"
        + username
        + '\''
        + ", score="
        + score
        + ", totalQuestions="
        + totalQuestions
        + ", startTime="
        + startTime
        + ", endTime="
        + endTime
        + ", isFinished="
        + isFinished
        + ", currentQuestion="
        + currentQuestion
        + ", questionNumber="
        + questionNumber
        + ", countriesLeft="
        + countriesLeft
        + '}';
  }
}
