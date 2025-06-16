package resenkov.work.todobackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import resenkov.work.todobackend.entity.Priority;
import resenkov.work.todobackend.entity.User;
import resenkov.work.todobackend.search.PrioritySearchValues;
import resenkov.work.todobackend.service.PriorityService;
import resenkov.work.todobackend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Controller
@RequestMapping("/priorities")
public class PriorityViewController {

    private final PriorityService priorityService;
    private final UserService userService;

    public PriorityViewController(PriorityService priorityService, UserService userService) {
        this.priorityService = priorityService;
        this.userService = userService;
    }

    @GetMapping
    public String prioritiesPage(Model model) {
        String email = getCurrentUserEmail();

        model.addAttribute("priority", new Priority());
        List<Priority> all = priorityService.findAll(email);
        model.addAttribute("priorities", all);
        model.addAttribute("searchValues", new PrioritySearchValues());

        return "priorities";
    }

    @PostMapping("/search")
    public String searchPriorities(@ModelAttribute("searchValues") PrioritySearchValues searchValues,
                                   Model model) {
        String email = getCurrentUserEmail();

        List<Priority> found = priorityService.findByTitle(searchValues.getTitle(), email);
        model.addAttribute("priority", new Priority());
        model.addAttribute("priorities", found);
        model.addAttribute("searchValues", searchValues);

        return "priorities";
    }

    @PostMapping("/add")
    public String addPriority(@ModelAttribute Priority priority,
                              RedirectAttributes redirectAttributes) {
        String email = getCurrentUserEmail();
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("Пользователь не найден");
            }
            priority.setUser(user);
            priorityService.add(priority);
            redirectAttributes.addFlashAttribute("successMessage", "Приоритет успешно добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении: " + e.getMessage());
        }
        return "redirect:/priorities";
    }

    @PostMapping("/update")
    public String updatePriority(@ModelAttribute Priority priority,
                                 RedirectAttributes redirectAttributes) {
        try {
            Priority existing = priorityService.findById(priority.getId());
            String email = getCurrentUserEmail();
            if (!existing.getUser().getEmail().equals(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Нет прав для редактирования");
                return "redirect:/priorities";
            }
            priority.setUser(existing.getUser());
            priorityService.update(priority);
            redirectAttributes.addFlashAttribute("successMessage", "Приоритет успешно обновлён!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
        }
        return "redirect:/priorities";
    }

    @GetMapping("/edit/{id}")
    public String editPriorityPage(@PathVariable Long id, Model model) {
        String email = getCurrentUserEmail();
        Priority priority = priorityService.findById(id);

        if (!priority.getUser().getEmail().equals(email)) {
            return "redirect:/priorities";
        }

        model.addAttribute("priority", priority);
        model.addAttribute("priorities", priorityService.findAll(email));
        model.addAttribute("searchValues", new PrioritySearchValues());

        return "priorities";
    }

    @GetMapping("/delete/{id}")
    public String deletePriority(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        String email = getCurrentUserEmail();
        Priority priority = priorityService.findById(id);

        if (!priority.getUser().getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Нет прав для удаления");
            return "redirect:/priorities";
        }
        try {
            priorityService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Приоритет успешно удалён!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/priorities";
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
