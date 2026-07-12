package pl.project.benchmark.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.project.benchmark.common.persistence.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
