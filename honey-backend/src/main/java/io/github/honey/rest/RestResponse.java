package io.github.honey.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.honey.either.Either;
import io.javalin.http.HttpStatus;

public final class RestResponse {

  private int status;
  private String message;

  @JsonCreator
  public RestResponse() {}

  private RestResponse(final int status, final String message) {
    this.status = status;
    this.message = message;
  }

  private RestResponse(final HttpStatus code, final String message) {
    this(code.getCode(), message);
  }

  public static RestResponse create(final HttpStatus status) {
    return create(status, null);
  }

  public static RestResponse create(final HttpStatus status, final String message) {
    return new RestResponse(status, message != null ? message : status.getMessage());
  }

  public static <V> Either<RestResponse, V> success(final V value) {
    return Either.right(value);
  }

  public static RestResponse notFound(final String message) {
    return create(HttpStatus.NOT_FOUND, message);
  }

  public static RestResponse notFound() {
    return notFound(null);
  }

  public static RestResponse unauthorized(final String message) {
    return create(HttpStatus.UNAUTHORIZED, message);
  }

  public static RestResponse unauthorized() {
    return unauthorized(null);
  }

  public static RestResponse badRequest(final String message) {
    return create(HttpStatus.BAD_REQUEST, message);
  }

  public static RestResponse badRequest() {
    return badRequest(null);
  }

  public static RestResponse internalServer() {
    return internalServer(null);
  }

  public static RestResponse internalServer(final String message) {
    return create(HttpStatus.INTERNAL_SERVER_ERROR, message);
  }

  public int status() {
    return status;
  }

  public String message() {
    return message;
  }

  public <V> Either<RestResponse, V> either() {
    return Either.left(this);
  }
}
