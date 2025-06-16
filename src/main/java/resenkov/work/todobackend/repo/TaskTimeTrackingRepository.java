package resenkov.work.todobackend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import resenkov.work.todobackend.entity.TaskTimeTracking;

import java.util.List;

@Repository
public interface TaskTimeTrackingRepository extends JpaRepository<TaskTimeTracking, Long> {

    List<TaskTimeTracking> findByTaskId(Long taskId);

    TaskTimeTracking findTopByTaskIdOrderByStartTimeDesc(Long taskId);
}
