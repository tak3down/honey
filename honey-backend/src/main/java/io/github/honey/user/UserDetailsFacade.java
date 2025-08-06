package io.github.honey.user;

public interface UserDetailsFacade {

  UserDetails authenticateUser(final String username, final String password);

  UserDetails registerUser(final String username, final String password);

  UserDetails getUserByUsername(final String username);
}
