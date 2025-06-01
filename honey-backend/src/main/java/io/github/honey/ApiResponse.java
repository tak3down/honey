package io.github.honey;

import io.javalin.http.HttpStatus;

public final class ApiResponse {

  private int status;
  private String message;

  public ApiResponse() {}

  private ApiResponse(final int status, final String message) {
    this.status = status;
    this.message = message;
  }

  private ApiResponse(final HttpStatus code, final String message) {
    this(code.getCode(), message);
  }

  public static ApiResponse of(final HttpStatus status) {
    return of(status, null);
  }

  public static ApiResponse of(final HttpStatus status, final String message) {
    return new ApiResponse(status, message != null ? message : status.getMessage());
  }

  public static <V> Either<ApiResponse, V> success(final V value) {
    return Either.right(value);
  }

  public static HtmlResponse html(final String content) {
    return new HtmlResponse(content);
  }

  public static <V> Either<ApiResponse, V> notFoundError(final String message) {
    return notFound(message).toEither();
  }

  public static <V> Either<ApiResponse, V> notFoundError() {
    return notFoundError(null);
  }

  public static ApiResponse notFound(final String message) {
    return of(HttpStatus.NOT_FOUND, message);
  }

  public static ApiResponse notFound() {
    return notFound(null);
  }

  public static <V> Either<ApiResponse, V> unauthorizedError(final String message) {
    return unauthorized(message).toEither();
  }

  public static <V> Either<ApiResponse, V> unauthorizedError() {
    return unauthorizedError(null);
  }

  public static ApiResponse unauthorized(final String message) {
    return of(HttpStatus.UNAUTHORIZED, message);
  }

  public static ApiResponse unauthorized() {
    return unauthorized(null);
  }

  public static <V> Either<ApiResponse, V> badRequestError(final String message) {
    return badRequest(message).toEither();
  }

  public static <V> Either<ApiResponse, V> badRequestError() {
    return badRequestError(null);
  }

  public static ApiResponse badRequest(final String message) {
    return of(HttpStatus.BAD_REQUEST, message);
  }

  public static ApiResponse badRequest() {
    return badRequest(null);
  }

  public static ApiResponse internalServer() {
    return internalServer(null);
  }

  public static ApiResponse internalServer(final String message) {
    return of(HttpStatus.INTERNAL_SERVER_ERROR, message);
  }

  public static <V> Either<ApiResponse, V> internalServerError(final String message) {
    return internalServer(message).toEither();
  }

  public static <V> Either<ApiResponse, V> internalServerError() {
    return internalServerError(null);
  }

  public int status() {
    return status;
  }

  public String message() {
    return message;
  }

  public <V> Either<ApiResponse, V> toEither() {
    return Either.left(this);
  }

  @Override
  public String toString() {
    return "ApiResponse{" + "status=" + status + ", message='" + message + '\'' + '}';
  }
}
