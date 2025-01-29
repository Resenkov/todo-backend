package resenkov.work.todobackend.controller;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resenkov.work.todobackend.entity.Priority;
import resenkov.work.todobackend.search.PrioritySearchValues;
import resenkov.work.todobackend.service.CategoryService;
import resenkov.work.todobackend.service.PriorityService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/priority")
public class PriorityController {

    private final CategoryService categoryService;
    private PriorityService priorityService;
    public PriorityController(PriorityService priorityService, CategoryService categoryService) {
        this.priorityService = priorityService;
        this.categoryService = categoryService;
    }

    @PostMapping("/all")
    public List<Priority> getAllPriority(@RequestBody String email) {
        return priorityService.findAll(email);
    }

    @PostMapping("/add")
    public ResponseEntity<Priority> addPriority(@RequestBody Priority priority) {
        if(priority.getTitle()== null || priority.getTitle().trim().length() == 0){
            return new ResponseEntity("Title must be not empty", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(priorityService.add(priority));
    }

    @PutMapping("/update")
    public ResponseEntity<Priority> updatePriority(@RequestBody Priority priority) {
        if(priority.getTitle()== null || priority.getTitle().trim().length() == 0){
            return new ResponseEntity("Title must be not empty", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(priorityService.update(priority));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Priority> deletePriority(@PathVariable Long id) {
        try {
            categoryService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping("/search")
    public ResponseEntity<List<Priority>> searchPriority(@RequestBody PrioritySearchValues prioritySearchValues) {
        if(prioritySearchValues.getEmail() == null || prioritySearchValues.getEmail().trim().length() == 0){
            return new ResponseEntity("Email must be not empty", HttpStatus.NOT_ACCEPTABLE);
        }
        List<Priority> list = priorityService.findByTitle(prioritySearchValues.getTitle(), prioritySearchValues.getEmail());
        return  ResponseEntity.ok(list);
    }

    @PostMapping("/id")
    public ResponseEntity<Priority> getPriorityById(@RequestBody Long id) {

        Priority priority = null;
        try {
            priority = priorityService.findById(id);
        }catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(priority);
    }
}
