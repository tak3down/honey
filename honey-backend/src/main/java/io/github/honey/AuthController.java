package io.github.honey;

import static io.github.honey.ApiResponse.badRequestError;
import static io.github.honey.Either.right;
import static io.github.honey.HoneyController.responseEither;

import io.javalin.community.routing.Route;
import io.javalin.http.Context;

public final class AuthController extends HoneyControllerRegistry {

  private final UserDetailsService userDetailsService;

  public AuthController(final UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;

    routes(new HoneyController("/api/auth/login", responseEither(this::login), Route.POST));
    routes(new HoneyController("/api/auth/register", responseEither(this::register), Route.POST));
  }

  private Either<ApiResponse, UserDetails> login(final Context context) {
    final AuthRequest request = context.bodyAsClass(AuthRequest.class);
    final UserDetails userDetails =
        userDetailsService.authenticate(request.getUsername(), request.getPassword());
    if (userDetails != null) {
      return right(userDetails);
    }

    return badRequestError("Invalid credentials");
  }

  private Either<ApiResponse, UserDetails> register(final Context context) {
    final AuthRequest request = context.bodyAsClass(AuthRequest.class);
    final UserDetails userDetails =
        userDetailsService.registerUser(request.getUsername(), request.getPassword());
    if (userDetails != null) {
      return right(userDetails);
    }

    return badRequestError("Username already exists");
  }
}
