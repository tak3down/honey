package io.github.honey.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserDetailsRepository extends JpaRepository<UserDetails, String> {}
