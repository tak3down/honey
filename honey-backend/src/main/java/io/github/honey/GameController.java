package io.github.honey;

import static io.github.honey.HoneyController.response;
import static io.github.honey.HoneyController.responseEither;
import static io.github.honey.shared.ApiResponse.badRequestError;
import static io.github.honey.shared.ApiResponse.notFoundError;
import static io.github.honey.shared.Either.right;

import io.github.honey.shared.ApiResponse;
import io.github.honey.shared.Either;
import io.javalin.community.routing.Route;
import io.javalin.http.Context;
import java.util.List;

final class GameController extends HoneyControllerRegistry {

  private final GameService gameService;
  private final UserDetailsService userDetailsService;

  GameController(final GameService gameService, final UserDetailsService userDetailsService) {
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

    return session == null ? badRequestError("Invalid session") : right(session);
  }

  private List<LeaderboardEntry> getLeaderboard(final Context context) {
    return gameService.getLeaderboard();
  }

  private Either<ApiResponse, GameSession> getSession(final Context context) {
    final String sessionId = context.pathParam("sessionId");
    final GameSession session = gameService.getActiveSessionById(sessionId);

    return session == null ? notFoundError() : right(session);
  }
}
