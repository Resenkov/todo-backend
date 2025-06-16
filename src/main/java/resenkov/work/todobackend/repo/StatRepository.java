package resenkov.work.todobackend.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import resenkov.work.todobackend.entity.Stat;


@Repository
public interface StatRepository extends CrudRepository<Stat, Long> {
    Stat findByUserEmail(String email);
}
