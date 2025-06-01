package io.github.honey;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

final class GameService {

  private static final int MAX_LEADERBOARD_SIZE = 50;

  private final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();
  private final List<LeaderboardEntry> leaderboard = new ArrayList<>();

  private final List<String> countries = new ArrayList<>();
  private final Map<String, String> countryFlags = new HashMap<>();

  GameService(final HoneyConfig honeyConfig) {
    countryFlags.putAll(honeyConfig.countryFlags);
    countries.addAll(honeyConfig.countryFlags.keySet());
  }

  GameSession startNewGame(final String username) {
    final String sessionId = UUID.randomUUID().toString();
    final GameSession session = new GameSession(sessionId, username);

    final GameQuestion firstQuestion = generateQuestion(1);
    session.setCurrentQuestion(firstQuestion);

    activeSessions.put(sessionId, session);
    return session;
  }

  GameSession submitAnswer(final String sessionId, final String answer) {
    final GameSession session = activeSessions.get(sessionId);
    if (session == null) {
      return null;
    }

    final boolean isCorrect = session.getCurrentQuestion().getCorrectCountry().equals(answer);
    if (isCorrect) {
      session.setScore(session.getScore() + 1);
    }

    if (session.getQuestionNumber() >= 10) {
      finishGame(session);
    } else {

      final int nextQuestionNumber = session.getQuestionNumber() + 1;
      final GameQuestion nextQuestion = generateQuestion(nextQuestionNumber);

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

  private GameQuestion generateQuestion(final int questionNumber) {
    final String correctCountry =
        countries.get(ThreadLocalRandom.current().nextInt(countries.size()));
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

    shuffle(options);

    return new GameQuestion(flagUrl, correctCountry, options, questionNumber);
  }

  List<LeaderboardEntry> getLeaderboard() {
    return leaderboard.stream()
        .sorted(
            (a, b) -> {
              final int scoreCompare = Integer.compare(b.getScore(), a.getScore());
              if (scoreCompare == 0) {
                return Long.compare(a.getTimeElapsed(), b.getTimeElapsed());
              }
              return scoreCompare;
            })
        .limit(MAX_LEADERBOARD_SIZE)
        .collect(toList());
  }

  GameSession getSession(final String sessionId) {
    return activeSessions.get(sessionId);
  }
}
