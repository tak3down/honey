package io.github.honey.game;

import java.util.List;

public class GameQuestion {
  private String flagUrl;
  private String correctCountry;
  private List<String> options;
  private int questionNumber;

  public GameQuestion() {}

  public GameQuestion(
      String flagUrl,
      String correctCountry,
      List<String> options,
      int questionNumber) {
    this.flagUrl = flagUrl;
    this.correctCountry = correctCountry;
    this.options = options;
    this.questionNumber = questionNumber;
  }

  public String getFlagUrl() {
    return flagUrl;
  }

  public void setFlagUrl(String flagUrl) {
    this.flagUrl = flagUrl;
  }

  public String getCorrectCountry() {
    return correctCountry;
  }

  public void setCorrectCountry(String correctCountry) {
    this.correctCountry = correctCountry;
  }

  public List<String> getOptions() {
    return options;
  }

  public void setOptions(List<String> options) {
    this.options = options;
  }

  public int getQuestionNumber() {
    return questionNumber;
  }

  public void setQuestionNumber(int questionNumber) {
    this.questionNumber = questionNumber;
  }
}
