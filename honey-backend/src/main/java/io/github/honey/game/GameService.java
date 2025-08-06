package io.github.honey.game;

import io.github.honey.config.HoneyConfig;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
final class GameService implements GameFacade {

  private final List<LeaderboardEntry> leaderboard = new ArrayList<>();
  private final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();
  private final List<String> countries = new ArrayList<>();
  private final Map<String, String> countryFlags = new HashMap<>();

  @Autowired
  GameService(final HoneyConfig honeyConfig) {
    countryFlags.putAll(honeyConfig.countryFlags);
    countries.addAll(honeyConfig.countryFlags.keySet());
  }

  @Override
  public GameSession getActiveSessionById(final String sessionId) {
    return activeSessions.get(sessionId);
  }

  @Override
  public List<LeaderboardEntry> getLeaderboard() {
    return leaderboard.stream()
        .sorted(
            (a, b) -> {
              final int scoreCompare = Integer.compare(b.getScore(), a.getScore());
              if (scoreCompare == 0) {
                return Long.compare(a.getTimeElapsed(), b.getTimeElapsed());
              }
              return scoreCompare;
            })
        .limit(50)
        .collect(Collectors.toList());
  }

  @Override
  public GameSession startNewGame(final String username) {
    final String sessionId = UUID.randomUUID().toString();
    final GameSession session = new GameSession(sessionId, username);

    final GameQuestion firstQuestion = generateQuestion(session, 1);
    session.setCurrentQuestion(firstQuestion);

    activeSessions.put(sessionId, session);

    return session;
  }

  @Override
  public GameSession submitAnswer(final String sessionId, final String answer) {
    final GameSession session = activeSessions.get(sessionId);
    if (session == null) {
      return null;
    }

    final boolean isCorrect = session.getCurrentQuestion().correctCountry().equals(answer);
    if (isCorrect) {
      session.setScore(session.getScore() + 1);
    }

    if (session.getQuestionNumber() >= 10) {
      finishGame(session);
    } else {

      final int nextQuestionNumber = session.getQuestionNumber() + 1;
      final GameQuestion nextQuestion = generateQuestion(session, nextQuestionNumber);

      session.setCurrentQuestion(nextQuestion);
      session.setQuestionNumber(nextQuestionNumber);
    }

    return session;
  }

  private void finishGame(final GameSession session) {
    session.setFinished(true);
    session.setEndTime(System.currentTimeMillis());

    final long timeElapsed = session.getEndTime() - session.getStartTime();

    final LeaderboardEntry entry =
        new LeaderboardEntry(
            session.getUsername(), session.getScore(), timeElapsed, LocalDateTime.now());

    boolean shouldSort = true;
    boolean isNewEntry = true;

    for (final LeaderboardEntry leaderboardEntry : leaderboard) {
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
    }

    if (isNewEntry) {
      leaderboard.add(entry);
    }

    if (shouldSort) {
      leaderboard.sort(
          (a, b) -> {
            final int scoreCompare = Integer.compare(b.getScore(), a.getScore());
            if (scoreCompare == 0) {
              return Long.compare(a.getTimeElapsed(), b.getTimeElapsed());
            }
            return scoreCompare;
          });
    }

    activeSessions.remove(session.getSessionId());
  }

  private GameQuestion generateQuestion(final GameSession session, final int questionNumber) {

    // used to avoid repetition of countries in session
    // for a better user experience :D

    final List<String> countriesLeft = session.getCountriesLeft();
    if (countriesLeft.isEmpty()) {
      countriesLeft.addAll(countries);
    }

    final String correctCountry =
        countriesLeft.get(ThreadLocalRandom.current().nextInt(countriesLeft.size()));

    // remove pooled country
    countriesLeft.remove(correctCountry);

    final String flagUrl = countryFlags.get(correctCountry);

    final List<String> options = new ArrayList<>();
    options.add(correctCountry);

    final List<String> availableCountries = new ArrayList<>(countries);
    availableCountries.remove(correctCountry);

    for (int i = 0; i < 3; i++) {
      final String wrongCountry =
          availableCountries.get(ThreadLocalRandom.current().nextInt(availableCountries.size()));
      options.add(wrongCountry);
      availableCountries.remove(wrongCountry);
    }

    Collections.shuffle(options);

    return new GameQuestion(flagUrl, correctCountry, options, questionNumber);
  }
}
