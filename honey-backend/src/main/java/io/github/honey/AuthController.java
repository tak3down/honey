package io.github.honey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

  @Autowired private UserService userService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody final AuthRequest request) {
    final User user = userService.authenticate(request.getUsername(), request.getPassword());
    if (user != null) {
      return ResponseEntity.ok(user);
    }
    return ResponseEntity.badRequest().body("Invalid credentials");
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody final AuthRequest request) {
    final User user = userService.registerUser(request.getUsername(), request.getPassword());
    if (user != null) {
      return ResponseEntity.ok(user);
    }
    return ResponseEntity.badRequest().body("Username already exists");
  }
}
