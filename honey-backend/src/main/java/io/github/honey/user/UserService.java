package io.github.honey.user;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public final class UserService {

  private final Map<String, User> users = new ConcurrentHashMap<>();

  public User authenticate(String username, String password) {
    User user = users.get(username);
    if (user != null && user.getPassword().equals(password))
      return user;
    return null;
  }

  public User registerUser(String username, String password) {
    if (users.containsKey(username))
      return null;

    User user = new User(username, password);
    users.put(username, user);
    return user;
  }

  public User getUserByUsername(String username) {
    return users.get(username);
  }

  public boolean userExists(String username) {
    return users.containsKey(username);
  }
}
