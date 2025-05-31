package io.github.honey.game;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

import io.github.honey.HoneyConfig;
import io.github.honey.leaderboard.LeaderboardEntry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public final class GameService {

  private static final int MAX_LEADERBOARD_SIZE = 50;

  private final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();
  private final List<LeaderboardEntry> leaderboard = new ArrayList<>();

  private final List<String> countries = new ArrayList<>();
  private final Map<String, String> countryFlags = new HashMap<>();

  public GameService(HoneyConfig honeyConfig) {
    countryFlags.putAll(honeyConfig.countryFlags);
    countries.addAll(honeyConfig.countryFlags.keySet());
  }

  public GameSession startNewGame(String username) {
    String sessionId = UUID.randomUUID().toString();
    GameSession session = new GameSession(sessionId, username);

    GameQuestion firstQuestion = generateQuestion(1);
    session.setCurrentQuestion(firstQuestion);

    activeSessions.put(sessionId, session);
    return session;
  }

  public GameSession submitAnswer(String sessionId, String answer) {
    GameSession session = activeSessions.get(sessionId);
    // || session.isFinished()
    if (session == null) return null;

    boolean isCorrect = session.getCurrentQuestion().getCorrectCountry().equals(answer);
    if (isCorrect) session.setScore(session.getScore() + 1);

    if (session.getQuestionNumber() >= 10) finishGame(session);
    else {
      int nextQuestionNumber = session.getQuestionNumber() + 1;
      GameQuestion nextQuestion = generateQuestion(nextQuestionNumber);
      session.setCurrentQuestion(nextQuestion);
      session.setQuestionNumber(nextQuestionNumber);
    }

    return session;
  }

  private void finishGame(GameSession session) {
    session.setFinished(true);
    session.setEndTime(System.currentTimeMillis());

    long timeElapsed = session.getEndTime() - session.getStartTime();

    LeaderboardEntry entry =
        new LeaderboardEntry(
            session.getUsername(), session.getScore(), timeElapsed, LocalDateTime.now());

    boolean shouldSort = true;
    boolean isNewEntry = true;

    for (LeaderboardEntry leaderboardEntry : leaderboard)
      if (leaderboardEntry.getUsername().equals(session.getUsername())) {
        isNewEntry = false;

        if (leaderboardEntry.compareTo(entry) <= 0) { // is less than or equal to
          shouldSort = false;
          continue;
        }

        leaderboardEntry.setScore(entry.getScore());
        leaderboardEntry.setTimeElapsed(entry.getTimeElapsed());
        leaderboardEntry.setCompletedAt(entry.getCompletedAt());

        break;
      }

    if (isNewEntry) leaderboard.add(entry);

    if (shouldSort)
      leaderboard.sort(
          (a, b) -> {
            int scoreCompare = Integer.compare(b.getScore(), a.getScore());
            if (scoreCompare == 0) return Long.compare(a.getTimeElapsed(), b.getTimeElapsed());
            return scoreCompare;
          });

    activeSessions.remove(session.getSessionId());
  }

  private GameQuestion generateQuestion(int questionNumber) {
    String correctCountry = countries.get(ThreadLocalRandom.current().nextInt(countries.size()));
    String flagUrl = countryFlags.get(correctCountry);

    List<String> options = new ArrayList<>();
    options.add(correctCountry);

    List<String> availableCountries = new ArrayList<>(countries);
    availableCountries.remove(correctCountry);

    for (int i = 0; i < 3; i++) {
      String wrongCountry =
          availableCountries.get(ThreadLocalRandom.current().nextInt(availableCountries.size()));
      options.add(wrongCountry);
      availableCountries.remove(wrongCountry);
    }

    shuffle(options);

    return new GameQuestion(flagUrl, correctCountry, options, questionNumber);
  }

  public List<LeaderboardEntry> getLeaderboard() {
    return leaderboard.stream()
        .sorted(
            (a, b) -> {
              int scoreCompare = Integer.compare(b.getScore(), a.getScore());
              if (scoreCompare == 0) return Long.compare(a.getTimeElapsed(), b.getTimeElapsed());
              return scoreCompare;
            })
        .limit(MAX_LEADERBOARD_SIZE)
        .collect(toList());
  }

  public GameSession getSession(String sessionId) {
    return activeSessions.get(sessionId);
  }
}
