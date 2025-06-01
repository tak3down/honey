package io.github.honey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class UserDetailsService {

  private final Map<String, UserDetails> usernameToUserMap = new ConcurrentHashMap<>();

  UserDetails authenticate(final String username, final String password) {
    final UserDetails userDetails = usernameToUserMap.get(username);
    if (userDetails != null && userDetails.getPassword().equals(password)) {
      return userDetails;
    }
    return null;
  }

  UserDetails registerUser(final String username, final String password) {
    if (usernameToUserMap.containsKey(username)) {
      return null;
    }

    final UserDetails userDetails = new UserDetails(username, password);
    usernameToUserMap.put(username, userDetails);
    return userDetails;
  }

  UserDetails getUserByUsername(final String username) {
    return usernameToUserMap.get(username);
  }

  boolean userExists(final String username) {
    return usernameToUserMap.containsKey(username);
  }
}
