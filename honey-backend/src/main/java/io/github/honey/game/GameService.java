package io.github.honey.game;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

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

  private final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();
  private final List<LeaderboardEntry> leaderboard = new ArrayList<>();

  private final Map<String, String> countryFlags = new HashMap<>();
  private final List<String> countries = new ArrayList<>();

  public GameService() {
    initializeCountryData();
  }

  private void initializeCountryData() {
    countryFlags.put("Stany Zjednoczone", "https://flagcdn.com/w320/us.png");
    countryFlags.put("Wielka Brytania", "https://flagcdn.com/w320/gb.png");
    countryFlags.put("Niemcy", "https://flagcdn.com/w320/de.png");
    countryFlags.put("Francja", "https://flagcdn.com/w320/fr.png");
    countryFlags.put("Włochy", "https://flagcdn.com/w320/it.png");
    countryFlags.put("Hiszpania", "https://flagcdn.com/w320/es.png");
    countryFlags.put("Kanada", "https://flagcdn.com/w320/ca.png");
    countryFlags.put("Australia", "https://flagcdn.com/w320/au.png");
    countryFlags.put("Japonia", "https://flagcdn.com/w320/jp.png");
    countryFlags.put("Chiny", "https://flagcdn.com/w320/cn.png");
    countryFlags.put("Brazylia", "https://flagcdn.com/w320/br.png");
    countryFlags.put("Indie", "https://flagcdn.com/w320/in.png");
    countryFlags.put("Rosja", "https://flagcdn.com/w320/ru.png");
    countryFlags.put("Meksyk", "https://flagcdn.com/w320/mx.png");
    countryFlags.put("Polska", "https://flagcdn.com/w320/pl.png");
    countryFlags.put("Szwecja", "https://flagcdn.com/w320/se.png");
    countryFlags.put("Norwegia", "https://flagcdn.com/w320/no.png");
    countryFlags.put("Holandia", "https://flagcdn.com/w320/nl.png");
    countryFlags.put("Belgia", "https://flagcdn.com/w320/be.png");
    countryFlags.put("Szwajcaria", "https://flagcdn.com/w320/ch.png");
    countryFlags.put("Austria", "https://flagcdn.com/w320/at.png");
    countryFlags.put("Portugalia", "https://flagcdn.com/w320/pt.png");
    countryFlags.put("Grecja", "https://flagcdn.com/w320/gr.png");
    countryFlags.put("Turcja", "https://flagcdn.com/w320/tr.png");
    countryFlags.put("Egipt", "https://flagcdn.com/w320/eg.png");
    countryFlags.put("RPA", "https://flagcdn.com/w320/za.png");
    countryFlags.put("Argentyna", "https://flagcdn.com/w320/ar.png");
    countryFlags.put("Chile", "https://flagcdn.com/w320/cl.png");
    countryFlags.put("Kolumbia", "https://flagcdn.com/w320/co.png");
    countryFlags.put("Korea Południowa", "https://flagcdn.com/w320/kr.png");

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
    if (session == null) { // || session.isFinished()
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
    leaderboard.add(entry);

    leaderboard.sort(
        (a, b) -> {
          final int scoreCompare = Integer.compare(b.getScore(), a.getScore());
          if (scoreCompare == 0) {
            return Long.compare(a.getTimeElapsed(), b.getTimeElapsed());
          }
          return scoreCompare;
        });
  }

  public boolean invalidateSession(final String sessionId) {
    return activeSessions.remove(sessionId) != null;
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
