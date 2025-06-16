package resenkov.work.todobackend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import resenkov.work.todobackend.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}