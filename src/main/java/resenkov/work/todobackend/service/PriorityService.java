package resenkov.work.todobackend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import resenkov.work.todobackend.entity.Priority;
import resenkov.work.todobackend.repo.PriorityRepository;

import java.util.List;


@Service
@Transactional

public class PriorityService {

    PriorityRepository priorityRepository;
    public PriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    public List<Priority> findAll(String email) {
        return priorityRepository.findByUserEmailOrderByTitleAsc(email);
    }

    public Priority add(Priority priority) {
        return priorityRepository.save(priority);
    }

    public void deleteById(Long id) {
        priorityRepository.deleteById(id);
    }

    public Priority update(Priority priority) {
        return priorityRepository.save(priority);
    }

    public List<Priority> findByTitle(String title, String email) {
        return priorityRepository.findByTitle(title, email);
    }

    public Priority findById(Long id) {
        return priorityRepository.findById(id).get();
    }

}