package io.github.honey.game;

import java.util.List;

public interface GameFacade {

  GameSession startNewGame(String username);

  GameSession submitAnswer(String sessionId, String answer);

  GameSession getActiveSessionById(String sessionId);

  List<LeaderboardEntry> getLeaderboard();
}
