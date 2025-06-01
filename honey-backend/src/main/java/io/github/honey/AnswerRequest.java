package io.github.honey;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class AnswerRequest {

  private String sessionId;
  private String answer;

  @JsonCreator
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
