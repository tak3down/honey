package io.github.honey;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class GameService {
  private final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();
  private final List<LeaderboardEntry> leaderboard = new ArrayList<>();

  // Country data for the game
  private final Map<String, String> countryFlags = new HashMap<>();
  private final List<String> countries = new ArrayList<>();

  public GameService() {
    initializeCountryData();
  }

  private void initializeCountryData() {
    // Initialize with some country data
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

    // Generate first question
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

    // Check if answer is correct
    final boolean isCorrect = session.getCurrentQuestion().getCorrectCountry().equals(answer);
    if (isCorrect) {
      session.setScore(session.getScore() + 1);
    }

    // Move to next question or finish game
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
    session.setEndTime(System.currentTimeMillis());

    // Add to leaderboard
    final long timeElapsed = session.getEndTime() - session.getStartTime();
    final LeaderboardEntry entry =
        new LeaderboardEntry(
            session.getUsername(), session.getScore(), timeElapsed, LocalDateTime.now());
    leaderboard.add(entry);

    // Sort leaderboard by score (desc) then by time (asc)
    leaderboard.sort(
        (a, b) -> {
          final int scoreCompare = Integer.compare(b.getScore(), a.getScore());
          if (scoreCompare == 0) {
            return Long.compare(a.getTimeElapsed(), b.getTimeElapsed());
          }
          return scoreCompare;
        });

    // Remove session from active sessions
    activeSessions.remove(session.getSessionId());
  }

  private GameQuestion generateQuestion(final int questionNumber) {
    final Random random = new Random();

    // Select correct country
    final String correctCountry = countries.get(random.nextInt(countries.size()));
    final String flagUrl = countryFlags.get(correctCountry);

    // Generate 3 wrong options
    final List<String> options = new ArrayList<>();
    options.add(correctCountry);

    final List<String> availableCountries = new ArrayList<>(countries);
    availableCountries.remove(correctCountry);

    for (int i = 0; i < 3; i++) {
      final String wrongCountry = availableCountries.get(random.nextInt(availableCountries.size()));
      options.add(wrongCountry);
      availableCountries.remove(wrongCountry);
    }

    // Shuffle options
    Collections.shuffle(options);

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
        .limit(50) // Top 50 entries
        .collect(Collectors.toList());
  }

  public GameSession getSession(final String sessionId) {
    return activeSessions.get(sessionId);
  }
}
