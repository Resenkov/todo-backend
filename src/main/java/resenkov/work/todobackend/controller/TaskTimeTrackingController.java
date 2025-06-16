package resenkov.work.todobackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resenkov.work.todobackend.entity.TaskTimeTracking;
import resenkov.work.todobackend.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/task-time-tracking")
public class TaskTimeTrackingController {

    private final TaskService taskService;

    public TaskTimeTrackingController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/start/{taskId}")
    public ResponseEntity<TaskTimeTracking> startTracking(@PathVariable Long taskId) {
        try {
            TaskTimeTracking tracking = taskService.startTracking(taskId);
            return ResponseEntity.ok(tracking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/stop/{taskId}")
    public ResponseEntity<TaskTimeTracking> stopTracking(@PathVariable Long taskId) {
        try {
            TaskTimeTracking tracking = taskService.stopTracking(taskId);
            return ResponseEntity.ok(tracking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/list/{taskId}")
    public ResponseEntity<List<TaskTimeTracking>> getTrackingList(@PathVariable Long taskId) {
        List<TaskTimeTracking> trackingList = taskService.getTrackingByTask(taskId);
        return ResponseEntity.ok(trackingList);
    }
}
