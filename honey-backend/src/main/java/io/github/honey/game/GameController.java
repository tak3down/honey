package io.github.honey.game;

import io.github.honey.leaderboard.LeaderboardEntry;
import io.github.honey.user.UserService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class GameController {

  private static final Logger log = LoggerFactory.getLogger(GameController.class);
  private final GameService gameService;
  private final UserService userService;

  public GameController(GameService gameService, UserService userService) {
    this.gameService = gameService;
    this.userService = userService;
  }

  @PostMapping("/game/start")
  public ResponseEntity<?> startGame(@RequestBody GameStartRequest request) {
    if (!userService.userExists(request.getUsername()))
      return ResponseEntity.badRequest().body("User not found");

    GameSession session = gameService.startNewGame(request.getUsername());
    return ResponseEntity.ok(session);
  }

  @PostMapping("/game/answer")
  public ResponseEntity<?> submitAnswer(@RequestBody AnswerRequest request) {
    GameSession session =
        gameService.submitAnswer(request.getSessionId(), request.getAnswer());
    if (session == null)
      return ResponseEntity.badRequest().body("Invalid session");
    return ResponseEntity.ok(session);
  }

  @GetMapping("/ranking")
  public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
    List<LeaderboardEntry> leaderboard = gameService.getLeaderboard();
    return ResponseEntity.ok(leaderboard);
  }

  @GetMapping("/game/session/{sessionId}")
  public ResponseEntity<?> getSession(@PathVariable String sessionId) {
    GameSession session = gameService.getSession(sessionId);
    if (session == null)
      return ResponseEntity.notFound().build();
    return ResponseEntity.ok(session);
  }
}
