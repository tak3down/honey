package io.github.honey.game;

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

  public String flagUrl() {
    return flagUrl;
  }

  public String correctCountry() {
    return correctCountry;
  }

  public List<String> options() {
    return options;
  }

  public int questionNumber() {
    return questionNumber;
  }
}
