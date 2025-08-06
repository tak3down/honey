package io.github.honey.user;

import io.github.honey.either.Either;
import io.github.honey.rest.RestContainer;
import io.github.honey.rest.RestResponse;
import io.github.honey.rest.RestRoute;
import io.javalin.community.routing.Route;
import io.javalin.http.Context;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;

@Controller
final class UserDetailsController extends RestContainer {

  private final UserDetailsFacade userDetailsFacade;

  UserDetailsController(final UserDetailsFacade userDetailsFacade) {
    this.userDetailsFacade = userDetailsFacade;
  }

  @PostConstruct
  private void registerRoutes() {
    registerRoute(
        new RestRoute("/api/auth/login", respondEither(this::login), Route.POST),
        new RestRoute("/api/auth/register", respondEither(this::register), Route.POST));
  }

  private Either<RestResponse, UserDetails> login(final Context context) {
    final AuthRequest request = context.bodyAsClass(AuthRequest.class);

    final UserDetails userDetails =
        userDetailsFacade.authenticateUser(request.username(), request.password());
    if (userDetails != null) {
      return Either.right(userDetails);
    }

    return RestResponse.badRequest("Invalid credentials").either();
  }

  private Either<RestResponse, UserDetails> register(final Context context) {
    final AuthRequest request = context.bodyAsClass(AuthRequest.class);

    final UserDetails userDetails =
        userDetailsFacade.registerUser(request.username(), request.password());
    if (userDetails != null) {
      return Either.right(userDetails);
    }

    return RestResponse.badRequest("Username already exists").either();
  }
}
