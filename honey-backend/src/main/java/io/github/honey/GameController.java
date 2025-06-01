package io.github.honey;

import static io.github.honey.ApiResponse.badRequestError;
import static io.github.honey.Either.right;
import static io.github.honey.HoneyController.response;
import static io.github.honey.HoneyController.responseEither;

import io.javalin.community.routing.Route;
import io.javalin.http.Context;
import java.util.List;

public final class GameController extends HoneyControllerRegistry {

  private final GameService gameService;
  private final UserDetailsService userDetailsService;

  public GameController(
      final GameService gameService, final UserDetailsService userDetailsService) {
    this.gameService = gameService;
    this.userDetailsService = userDetailsService;

    routes(
        new HoneyController("/api/game/start", responseEither(this::startGame), Route.POST),
        new HoneyController("/api/game/answer", responseEither(this::submitAnswer), Route.POST),
        new HoneyController("/api/ranking", response(this::getLeaderboard), Route.GET),
        new HoneyController(
            "/api/game/session/{sessionId}", responseEither(this::getSession), Route.GET));
  }

  private Either<ApiResponse, GameSession> startGame(final Context context) {
    final GameStartRequest request = context.bodyAsClass(GameStartRequest.class);
    if (!userDetailsService.userExists(request.getUsername())) {
      return badRequestError("UserDetails not found");
    }

    return right(gameService.startNewGame(request.getUsername()));
  }

  private Either<ApiResponse, GameSession> submitAnswer(final Context context) {
    final AnswerRequest request = context.bodyAsClass(AnswerRequest.class);

    final GameSession session =
        gameService.submitAnswer(request.getSessionId(), request.getAnswer());
    if (session == null) {
      return badRequestError("Invalid session");
    }

    return right(session);
  }

  private List<LeaderboardEntry> getLeaderboard(final Context context) {
    return gameService.getLeaderboard();
  }

  private Either<ApiResponse, GameSession> getSession(final Context context) {
    final String sessionId = context.pathParam("sessionId");
    final GameSession session = gameService.getSession(sessionId);
    if (session == null) {
      return ApiResponse.notFoundError();
    }
    return right(session);
  }
}
