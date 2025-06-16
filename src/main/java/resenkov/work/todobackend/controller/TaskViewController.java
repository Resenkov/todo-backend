package resenkov.work.todobackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import resenkov.work.todobackend.entity.Categories;
import resenkov.work.todobackend.entity.Priority;
import resenkov.work.todobackend.entity.Task;
import resenkov.work.todobackend.entity.User;
import resenkov.work.todobackend.search.TaskSearchValues;
import resenkov.work.todobackend.service.CategoryService;
import resenkov.work.todobackend.service.PriorityService;
import resenkov.work.todobackend.service.TaskService;
import resenkov.work.todobackend.service.UserService;

import java.time.LocalDate;
@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final PriorityService priorityService;
    private  final UserService userService;
    public TaskViewController(TaskService taskService,
                              CategoryService categoryService,
                              PriorityService priorityService, UserService userService) {
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.priorityService = priorityService;
        this.userService = userService;
    }

    @GetMapping
    public String tasksPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute TaskSearchValues searchValues,
            HttpServletRequest request,
            Model model) {

        String email = getCurrentUserEmail();
        searchValues.setEmail(email);

        LocalDate dateFrom = searchValues.getDateFrom();
        LocalDate dateTo   = searchValues.getDateTo();

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Page<Task> tasks = taskService.findByParams(
                searchValues.getTitle(),
                searchValues.getCompleted(),
                searchValues.getPriorityId(),
                searchValues.getCategoryId(),
                email,
                dateFrom,
                dateTo,
                pageable
        );

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", categoryService.findAll(email));
        model.addAttribute("priorities", priorityService.findAll(email));
        model.addAttribute("task", new Task());
        model.addAttribute("searchValues", searchValues);
        return "tasks";
    }


    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        String email = getCurrentUserEmail();

        try {
            User user = userService.findByEmail(email);
            task.setUser(user);

            if (task.getCategory() != null && task.getCategory().getId() != null) {
                Categories category = categoryService.findById(task.getCategory().getId());
                task.setCategory(category);
            } else {
                task.setCategory(null);
            }

            if (task.getPriority() != null && task.getPriority().getId() != null) {
                Priority priority = priorityService.findById(task.getPriority().getId());
                task.setPriority(priority);
            } else {
                task.setPriority(null);
            }

            taskService.add(task);
            redirectAttributes.addFlashAttribute("successMessage", "Задача успешно добавлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении задачи: " + e.getMessage());
        }

        return "redirect:/tasks";
    }


    @PostMapping("/update")
    public String updateTask(@ModelAttribute Task updatedTask, RedirectAttributes redirectAttributes) {
        String email = getCurrentUserEmail();

        try {
            Task existingTask = taskService.findById(updatedTask.getId());

            if (!existingTask.getUser().getEmail().equals(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Нет прав для редактирования");
                return "redirect:/tasks";
            }

            // Обновляем только необходимые поля
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDate(updatedTask.getDate());
            existingTask.setCompleted(updatedTask.getCompleted());

            // Обработка категории
            if (updatedTask.getCategory() != null && updatedTask.getCategory().getId() != null) {
                existingTask.setCategory(categoryService.findById(updatedTask.getCategory().getId()));
            } else {
                existingTask.setCategory(null);
            }

            // Обработка приоритета
            if (updatedTask.getPriority() != null && updatedTask.getPriority().getId() != null) {
                existingTask.setPriority(priorityService.findById(updatedTask.getPriority().getId()));
            } else {
                existingTask.setPriority(null);
            }

            taskService.update(existingTask);
            redirectAttributes.addFlashAttribute("successMessage", "Задача обновлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка обновления: " + e.getMessage());
        }

        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editTaskPage(
            @PathVariable Long id,
            HttpServletRequest request,
            Model model,
            @ModelAttribute("searchValues") TaskSearchValues searchValues  // добавили сюда
    ) {
        String email = getCurrentUserEmail();

        Task task = taskService.findById(id);
        if (!task.getUser().getEmail().equals(email)) {
            return "redirect:/tasks";
        }
        model.addAttribute("task", task);

        model.addAttribute("categories", categoryService.findAll(email));
        model.addAttribute("priorities", priorityService.findAll(email));

        model.addAttribute("currentPath", request.getRequestURI());

        searchValues.setEmail(email);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").descending());
        Page<Task> tasksPage = taskService.findByParams(
                searchValues.getTitle(),
                searchValues.getCompleted(),
                searchValues.getPriorityId(),
                searchValues.getCategoryId(),
                email,
                searchValues.getDateFrom(),
                searchValues.getDateTo(),
                pageable
        );
        model.addAttribute("tasks", tasksPage);

        model.addAttribute("searchValues", searchValues);

        return "tasks";
    }


    @DeleteMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String email = getCurrentUserEmail();
        Task task = taskService.findById(id);
        if (!task.getUser().getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Нет прав для удаления этой задачи");
            return "redirect:/tasks";
        }

        try {
            taskService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Задача успешно удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении задачи: " + e.getMessage());
        }

        return "redirect:/tasks";
    }
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}