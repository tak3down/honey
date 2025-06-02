package io.github.honey;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;

public final class GameQuestion {

  private String flagUrl;
  private String correctCountry;
  private List<String> options;
  private int questionNumber;

  @JsonCreator
  public GameQuestion() {}

  public GameQuestion(
      final String flagUrl,
      final String correctCountry,
      final List<String> options,
      final int questionNumber) {
    this.flagUrl = flagUrl;
    this.correctCountry = correctCountry;
    this.options = options;
    this.questionNumber = questionNumber;
  }

  public String getFlagUrl() {
    return flagUrl;
  }

  public String getCorrectCountry() {
    return correctCountry;
  }

  public List<String> getOptions() {
    return options;
  }

  public int getQuestionNumber() {
    return questionNumber;
  }

  @Override
  public String toString() {
    return "GameQuestion{"
        + "flagUrl='"
        + flagUrl
        + '\''
        + ", correctCountry='"
        + correctCountry
        + '\''
        + ", options="
        + options
        + ", questionNumber="
        + questionNumber
        + '}';
  }
}
