package resenkov.work.todobackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import resenkov.work.todobackend.entity.Categories;
import resenkov.work.todobackend.entity.User;
import resenkov.work.todobackend.search.CategorySearchValues;
import resenkov.work.todobackend.service.CategoryService;
import resenkov.work.todobackend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryViewController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryViewController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public String categoriesPage(Model model) {
        String email = getCurrentUserEmail();
        model.addAttribute("category", new Categories());
        List<Categories> all = categoryService.findAll(email);
        model.addAttribute("categories", all);
        model.addAttribute("searchValues", new CategorySearchValues());

        return "categories";
    }

    @PostMapping("/search")
    public String searchCategories(@ModelAttribute("searchValues") CategorySearchValues searchValues,
                                   Model model) {
        String email = getCurrentUserEmail();
        List<Categories> filtered = categoryService.findByTitle(searchValues.getTitle(), email);

        model.addAttribute("category", new Categories());
        model.addAttribute("categories", filtered);
        model.addAttribute("searchValues", searchValues);

        return "categories";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute Categories category,
                              RedirectAttributes redirectAttributes) {
        String email = getCurrentUserEmail();
        try {User user = userService.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("Пользователь не найден");
            }
            category.setUser(user);
            categoryService.add(category);
            redirectAttributes.addFlashAttribute("successMessage", "Категория успешно добавлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении: " + e.getMessage());
        }
        return "redirect:/categories";
    }
    @PostMapping("/update")
    public String updateCategory(@ModelAttribute Categories category,
                                 RedirectAttributes redirectAttributes) {
        try {
            Categories existing = categoryService.findById(category.getId());
            String email = getCurrentUserEmail();
            if (!existing.getUser().getEmail().equals(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Нет прав для редактирования");
                return "redirect:/categories";
            }
            category.setUser(existing.getUser());
            categoryService.update(category);
            redirectAttributes.addFlashAttribute("successMessage", "Категория обновлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategoryPage(@PathVariable Long id, Model model) {
        String email = getCurrentUserEmail();
        Categories category = categoryService.findById(id);

        if (!category.getUser().getEmail().equals(email)) {
            return "redirect:/categories";
        }
        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.findAll(email));
        model.addAttribute("searchValues", new CategorySearchValues());

        return "categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String email = getCurrentUserEmail();
        Categories category = categoryService.findById(id);

        if (!category.getUser().getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Нет прав для удаления");
            return "redirect:/categories";
        }
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Категория удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/categories";
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
