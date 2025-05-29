package io.github.honey;

import java.util.List;

public class GameQuestion {
  private String flagUrl;
  private String correctCountry;
  private List<String> options;
  private int questionNumber;

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

  // Getters and setters
  public String getFlagUrl() {
    return flagUrl;
  }

  public void setFlagUrl(final String flagUrl) {
    this.flagUrl = flagUrl;
  }

  public String getCorrectCountry() {
    return correctCountry;
  }

  public void setCorrectCountry(final String correctCountry) {
    this.correctCountry = correctCountry;
  }

  public List<String> getOptions() {
    return options;
  }

  public void setOptions(final List<String> options) {
    this.options = options;
  }

  public int getQuestionNumber() {
    return questionNumber;
  }

  public void setQuestionNumber(final int questionNumber) {
    this.questionNumber = questionNumber;
  }
}
