package resenkov.work.todobackend.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import resenkov.work.todobackend.entity.Task;
import resenkov.work.todobackend.repo.TaskRepository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import resenkov.work.todobackend.entity.TaskTimeTracking;
import resenkov.work.todobackend.repo.TaskTimeTrackingRepository;
import java.time.LocalDateTime;

@Service


@Transactional
public class TaskService {

    private final TaskRepository repository;

    private final TaskTimeTrackingRepository trackingRepository;

    @Autowired
    public TaskService(TaskRepository repository, TaskTimeTrackingRepository trackingRepository) {
        this.repository = repository;
        this.trackingRepository = trackingRepository;
    }


    public List<Task> findAll(String email) {
        return repository.findByUserEmailOrderByTitleAsc(email);
    }

    public Task add(Task task) {
        return repository.save(task);
    }

    public Task update(Task task) {
        Task existing = repository.findById(task.getId()).orElseThrow();
        existing.setTitle(task.getTitle());
        existing.setDate(task.getDate());
        existing.setCompleted(task.getCompleted());

        existing.setCategory(task.getCategory() != null ? task.getCategory() : null);
        existing.setPriority(task.getPriority() != null ? task.getPriority() : null);

        return repository.save(existing);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }


    public Page<Task> findByParams(String text, Boolean completed, Long priorityId, Long categoryId, String email, LocalDate dateFrom, LocalDate dateTo, Pageable paging) {
        return repository.findByParams(text, completed, priorityId, categoryId, email, dateFrom, dateTo, paging);
    }

    public Task findById(Long id) {
        return repository.findById(id).get();
    }

    public TaskTimeTracking startTracking(Long taskId) {
        Task task = findById(taskId);
        TaskTimeTracking tracking = new TaskTimeTracking();
        tracking.setTask(task);
        tracking.setStartTime(LocalDateTime.now());
        tracking.setEndTime(null);
        tracking.setDurationSeconds(null);
        return trackingRepository.save(tracking);
    }


    public TaskTimeTracking stopTracking(Long taskId) {
        TaskTimeTracking tracking = trackingRepository.findTopByTaskIdOrderByStartTimeDesc(taskId);
        if (tracking == null || tracking.getEndTime() != null) {
            throw new IllegalStateException("Трекинг для задачи не запущен или уже остановлен");
        }
        tracking.setEndTime(LocalDateTime.now());
        long duration = java.time.Duration.between(tracking.getStartTime(), tracking.getEndTime()).getSeconds();
        tracking.setDurationSeconds(duration);
        return trackingRepository.save(tracking);
    }

    public List<TaskTimeTracking> getTrackingByTask(Long taskId) {
        return trackingRepository.findByTaskId(taskId);
    }
}
