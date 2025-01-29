package resenkov.work.todobackend.service;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import resenkov.work.todobackend.entity.Stat;
import resenkov.work.todobackend.repo.StatRepository;

@Service
@Transactional
public class StatService {

    private final StatRepository repository; // сервис имеет право обращаться к репозиторию (БД)

    public StatService(StatRepository repository) {
        this.repository = repository;
    }

    public Stat findStat(String email) {
        return repository.findByUserEmail(email);
    }

}