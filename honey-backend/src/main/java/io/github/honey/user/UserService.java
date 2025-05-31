package io.github.honey.user;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public final class UserService {

  private final Map<String, User> users = new ConcurrentHashMap<>();

  public User authenticate(final String username, final String password) {
    final User user = users.get(username);
    if (user != null && user.getPassword().equals(password)) {
      return user;
    }
    return null;
  }

  public User registerUser(final String username, final String password) {
    if (users.containsKey(username)) {
      return null;
    }

    final User user = new User(username, password);
    users.put(username, user);
    return user;
  }

  public User getUserByUsername(final String username) {
    return users.get(username);
  }

  public boolean userExists(final String username) {
    return users.containsKey(username);
  }
}
