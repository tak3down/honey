package io.github.honey.game;

public class AnswerRequest {

  private String sessionId;
  private String answer;

  public AnswerRequest() {}

  public AnswerRequest(final String sessionId, final String answer) {
    this.sessionId = sessionId;
    this.answer = answer;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(final String sessionId) {
    this.sessionId = sessionId;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(final String answer) {
    this.answer = answer;
  }
}
