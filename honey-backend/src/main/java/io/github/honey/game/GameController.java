package io.github.honey.game;

import io.github.honey.leaderboard.LeaderboardEntry;
import io.github.honey.user.UserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class GameController {

  private final GameService gameService;
  private final UserService userService;

  public GameController(final GameService gameService, final UserService userService) {
    this.gameService = gameService;
    this.userService = userService;
  }

  @PostMapping("/game/start")
  public ResponseEntity<?> startGame(@RequestBody final GameStartRequest request) {
    if (!userService.userExists(request.getUsername())) {
      return ResponseEntity.badRequest().body("User not found");
    }

    final GameSession session = gameService.startNewGame(request.getUsername());
    return ResponseEntity.ok(session);
  }

  @PostMapping("/game/answer")
  public ResponseEntity<?> submitAnswer(@RequestBody final AnswerRequest request) {
    final GameSession session =
        gameService.submitAnswer(request.getSessionId(), request.getAnswer());
    if (session == null) {
      return ResponseEntity.badRequest().body("Invalid session");
    }
    return ResponseEntity.ok(session);
  }

  @GetMapping("/ranking")
  public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
    final List<LeaderboardEntry> leaderboard = gameService.getLeaderboard();
    return ResponseEntity.ok(leaderboard);
  }

  @GetMapping("/game/session/{sessionId}")
  public ResponseEntity<?> getSession(@PathVariable final String sessionId) {
    final GameSession session = gameService.getSession(sessionId);
    if (session == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(session);
  }
}
