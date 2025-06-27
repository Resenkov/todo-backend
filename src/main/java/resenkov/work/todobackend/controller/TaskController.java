package resenkov.work.todobackend.controller;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resenkov.work.todobackend.entity.Task;
import resenkov.work.todobackend.search.TaskSearchValues;
import resenkov.work.todobackend.service.TaskService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/task")
public class TaskController {

    public static final String ID_COLUMN = "id";

    TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping("/all")
    public ResponseEntity<List<Task>> findAll(@RequestBody String email) {
        return ResponseEntity.ok(taskService.findAll(email));
    }

    @PostMapping("/add")
    public ResponseEntity<Task> add(@RequestBody Task task) {
        if(task.getId() != null && task.getId() != 0) {
            return new ResponseEntity("ID CAN BE NULL", HttpStatus.NOT_ACCEPTABLE);
        }
        if (task.getTitle() == null || task.getTitle().trim().length() == 0) {
            return new ResponseEntity("TITLE CAN'T BE NULL", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(taskService.add(task));
    }


    @PutMapping("/update")
    public ResponseEntity<Task> update(@RequestBody Task task) {
        if(task.getId() == null && task.getId() == 0) {
            return new ResponseEntity("ID CANT BE NULL", HttpStatus.NOT_ACCEPTABLE);
        }
        if (task.getTitle() == null || task.getTitle().trim().length() == 0) {
            return new ResponseEntity("TITLE CAN'T BE NULL", HttpStatus.NOT_ACCEPTABLE);
        }
        taskService.update(task);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        try {
            taskService.deleteById(id);
        }catch (EmptyResultDataAccessException e){  
            e.printStackTrace();
            return new ResponseEntity("id" + id + " not found", HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/id")
    public ResponseEntity <Task> findById(@RequestBody Long id){
        Task task = null;

        try{
            task = taskService.findById(id);
        }catch (NoSuchElementException e){
            e.printStackTrace();
            return new ResponseEntity("id" + id + " not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<Task>> search(@RequestBody TaskSearchValues taskSearchValues) {
        String email = taskSearchValues.getEmail();
        if (email == null || email.trim().isEmpty()) {
            return new ResponseEntity("missed param: email", HttpStatus.NOT_ACCEPTABLE);
        }
        String title       = (taskSearchValues.getTitle()      != null) ? taskSearchValues.getTitle()     : null;
        Boolean completed  = taskSearchValues.getCompleted();
        Long priorityId    = taskSearchValues.getPriorityId();
        Long categoryId    = taskSearchValues.getCategoryId();
        String sortColumn  = taskSearchValues.getSortColumn();
        String sortDir     = taskSearchValues.getSortDirection();
        Integer pageNumber = taskSearchValues.getPageNumber() != null ? taskSearchValues.getPageNumber() : 0;
        Integer pageSize   = taskSearchValues.getPageSize()   != null ? taskSearchValues.getPageSize()   : 10;

        LocalDate ldFrom = taskSearchValues.getDateFrom();
        LocalDate ldTo   = taskSearchValues.getDateTo();

        Sort.Direction direction = (sortDir == null || sortDir.equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, (sortColumn == null ? "date" : sortColumn), ID_COLUMN);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        Page<Task> result = taskService.findByParams(
                title,
                completed,
                priorityId,
                categoryId,
                email,
                ldFrom,
                ldTo,
                pageRequest
        );

        return ResponseEntity.ok(result);
    }

}
