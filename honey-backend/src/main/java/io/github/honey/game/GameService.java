package io.github.honey.game;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

import io.github.honey.leaderboard.LeaderboardEntry;
import java.time.Duration;
import java.time.Instant;
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
  private final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();
  private final List<LeaderboardEntry> leaderboard = new ArrayList<>();

  private final Map<String, String> countryFlags = new HashMap<>();
  private final List<String> countries = new ArrayList<>();

  public GameService() {
    initializeCountryData();
  }

  private void initializeCountryData() {
    countryFlags.put("United States", "https://flagcdn.com/w320/us.png");
    countryFlags.put("United Kingdom", "https://flagcdn.com/w320/gb.png");
    countryFlags.put("Germany", "https://flagcdn.com/w320/de.png");
    countryFlags.put("France", "https://flagcdn.com/w320/fr.png");
    countryFlags.put("Italy", "https://flagcdn.com/w320/it.png");
    countryFlags.put("Spain", "https://flagcdn.com/w320/es.png");
    countryFlags.put("Canada", "https://flagcdn.com/w320/ca.png");
    countryFlags.put("Australia", "https://flagcdn.com/w320/au.png");
    countryFlags.put("Japan", "https://flagcdn.com/w320/jp.png");
    countryFlags.put("China", "https://flagcdn.com/w320/cn.png");
    countryFlags.put("Brazil", "https://flagcdn.com/w320/br.png");
    countryFlags.put("India", "https://flagcdn.com/w320/in.png");
    countryFlags.put("Russia", "https://flagcdn.com/w320/ru.png");
    countryFlags.put("Mexico", "https://flagcdn.com/w320/mx.png");
    countryFlags.put("Poland", "https://flagcdn.com/w320/pl.png");
    countryFlags.put("Sweden", "https://flagcdn.com/w320/se.png");
    countryFlags.put("Norway", "https://flagcdn.com/w320/no.png");
    countryFlags.put("Netherlands", "https://flagcdn.com/w320/nl.png");
    countryFlags.put("Belgium", "https://flagcdn.com/w320/be.png");
    countryFlags.put("Switzerland", "https://flagcdn.com/w320/ch.png");
    countryFlags.put("Austria", "https://flagcdn.com/w320/at.png");
    countryFlags.put("Portugal", "https://flagcdn.com/w320/pt.png");
    countryFlags.put("Greece", "https://flagcdn.com/w320/gr.png");
    countryFlags.put("Turkey", "https://flagcdn.com/w320/tr.png");
    countryFlags.put("Egypt", "https://flagcdn.com/w320/eg.png");
    countryFlags.put("South Africa", "https://flagcdn.com/w320/za.png");
    countryFlags.put("Argentina", "https://flagcdn.com/w320/ar.png");
    countryFlags.put("Chile", "https://flagcdn.com/w320/cl.png");
    countryFlags.put("Colombia", "https://flagcdn.com/w320/co.png");
    countryFlags.put("South Korea", "https://flagcdn.com/w320/kr.png");

    countries.addAll(countryFlags.keySet());
  }

  public GameSession startNewGame(final String username) {
    final String sessionId = UUID.randomUUID().toString();
    final GameSession session = new GameSession(sessionId, username);

    final GameQuestion firstQuestion = generateQuestion(1);
    session.setCurrentQuestion(firstQuestion);

    activeSessions.put(sessionId, session);
    return session;
  }

  public GameSession submitAnswer(final String sessionId, final String answer) {
    final GameSession session = activeSessions.get(sessionId);
    if (session == null || session.isFinished()) {
      return null;
    }

    final boolean isCorrect = session.getCurrentQuestion().getCorrectCountry().equals(answer);
    if (isCorrect) {
      session.setScore(session.getScore() + 1);
    }

    if (session.getQuestionNumber() >= 20) {
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
    session.setEndTime(Instant.now());

    final Duration timeElapsed = Duration.between(session.getStartTime(), session.getEndTime());
    final LeaderboardEntry entry =
        new LeaderboardEntry(
            session.getUsername(), session.getScore(), timeElapsed, LocalDateTime.now());
    leaderboard.add(entry);

    leaderboard.sort(
        (a, b) -> {
          final int scoreCompare = Integer.compare(b.getScore(), a.getScore());
          if (scoreCompare == 0) {
            return Long.compare(a.getTimeElapsed(), b.getTimeElapsed());
          }
          return scoreCompare;
        });

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
        .collect(toList());
  }

  public GameSession getSession(final String sessionId) {
    return activeSessions.get(sessionId);
  }
}
