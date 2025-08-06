package io.github.honey.user;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
final class UserDetailsService implements UserDetailsFacade {

  private final UserDetailsRepository userDetailsRepository;
  private final Map<String, UserDetails> usernameToUserMap = new ConcurrentHashMap<>();

  UserDetailsService(final UserDetailsRepository userDetailsRepository) {
    this.userDetailsRepository = userDetailsRepository;
  }

  @PostConstruct
  private void fetchAllUsers() {
    userDetailsRepository
        .findAll()
        .forEach(userDetails -> usernameToUserMap.put(userDetails.username(), userDetails));
  }

  @Override
  public UserDetails authenticateUser(final String username, final String password) {
    final UserDetails userDetails = getUserByUsername(username);
    if (userDetails != null && userDetails.password().equals(password)) {
      return userDetails;
    }
    return null;
  }

  @Override
  public UserDetails registerUser(final String username, final String password) {
    if (usernameToUserMap.containsKey(username)) {
      return null;
    }

    final UserDetails userDetails = new UserDetails(username, password);

    usernameToUserMap.put(username, userDetails);
    userDetailsRepository.save(userDetails);

    return userDetails;
  }

  @Override
  public UserDetails getUserByUsername(final String username) {
    return usernameToUserMap.get(username);
  }
}
