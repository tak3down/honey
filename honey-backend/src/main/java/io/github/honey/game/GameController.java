package io.github.honey.game;

import io.github.honey.either.Either;
import io.github.honey.rest.RestContainer;
import io.github.honey.rest.RestResponse;
import io.github.honey.rest.RestRoute;
import io.github.honey.user.UserDetailsFacade;
import io.javalin.community.routing.Route;
import io.javalin.http.Context;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
final class GameController extends RestContainer {

  private final GameFacade gameFacade;
  private final UserDetailsFacade userDetailsFacade;

  @Autowired
  GameController(final GameFacade gameFacade, final UserDetailsFacade userDetailsFacade) {
    this.gameFacade = gameFacade;
    this.userDetailsFacade = userDetailsFacade;
  }

  @PostConstruct
  private void registerRoutes() {
    registerRoute(
        new RestRoute("/api/game/start", respondEither(this::startGame), Route.POST),
        new RestRoute("/api/game/answer", respondEither(this::submitAnswer), Route.POST),
        new RestRoute("/api/ranking", respond(this::getLeaderboard), Route.GET),
        new RestRoute("/api/game/session/{sessionId}", respondEither(this::getSession), Route.GET));
  }

  private Either<RestResponse, GameSession> startGame(final Context context) {
    final GameStartRequest request = context.bodyAsClass(GameStartRequest.class);

    return userDetailsFacade.getUserByUsername(request.username()) == null
        ? RestResponse.badRequest("User details not found").either()
        : Either.right(gameFacade.startNewGame(request.username()));
  }

  private Either<RestResponse, GameSession> submitAnswer(final Context context) {
    final AnswerRequest request = context.bodyAsClass(AnswerRequest.class);

    final GameSession session = gameFacade.submitAnswer(request.sessionId(), request.answer());
    return session == null
        ? RestResponse.badRequest("Invalid session").either()
        : Either.right(session);
  }

  private Either<RestResponse, GameSession> getSession(final Context context) {
    final String sessionId = context.pathParam("sessionId");

    final GameSession session = gameFacade.getActiveSessionById(sessionId);
    return session == null
        ? RestResponse.notFound("Session %s couldn't be found".formatted(sessionId)).either()
        : Either.right(session);
  }

  private List<LeaderboardEntry> getLeaderboard(final Context context) {
    return gameFacade.getLeaderboard();
  }
}
