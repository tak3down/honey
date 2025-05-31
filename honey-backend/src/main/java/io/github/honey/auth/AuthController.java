package io.github.honey.auth;

import io.github.honey.user.User;
import io.github.honey.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    User user = userService.authenticate(request.getUsername(), request.getPassword());
    if (user != null)
      return ResponseEntity.ok(user);
    return ResponseEntity.badRequest().body("Invalid credentials");
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody AuthRequest request) {
    User user = userService.registerUser(request.getUsername(), request.getPassword());
    if (user != null)
      return ResponseEntity.ok(user);
    return ResponseEntity.badRequest().body("Username already exists");
  }
}
