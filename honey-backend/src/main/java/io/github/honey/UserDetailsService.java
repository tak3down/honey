package io.github.honey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UserDetailsService {

  private final Map<String, UserDetails> usernameToUserMap = new ConcurrentHashMap<>();

  public UserDetails authenticate(final String username, final String password) {
    final UserDetails userDetails = usernameToUserMap.get(username);
    if (userDetails != null && userDetails.getPassword().equals(password)) {
      return userDetails;
    }
    return null;
  }

  public UserDetails registerUser(final String username, final String password) {
    if (usernameToUserMap.containsKey(username)) {
      return null;
    }

    final UserDetails userDetails = new UserDetails(username, password);
    usernameToUserMap.put(username, userDetails);
    return userDetails;
  }

  public UserDetails getUserByUsername(final String username) {
    return usernameToUserMap.get(username);
  }

  public boolean userExists(final String username) {
    return usernameToUserMap.containsKey(username);
  }
}
