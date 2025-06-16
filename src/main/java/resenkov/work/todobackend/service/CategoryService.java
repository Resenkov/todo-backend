package resenkov.work.todobackend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import resenkov.work.todobackend.entity.Categories;
import resenkov.work.todobackend.repo.CategoryRepository;

import java.util.List;


@Service
@Transactional
public class CategoryService {

    private final CategoryRepository repository; // сервис имеет право обращаться к репозиторию (БД)

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Categories> findAll(String email) {
        return repository.findByUserEmailOrderByTitleAsc(email);
    }

    public Categories add(Categories category) {
        return repository.save(category);
    }

    public Categories update(Categories category){
        return repository.save(category);
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }

    public List<Categories> findByTitle(String title, String email) {
        return repository.findByTitle(title, email);
    }

    public Categories findById(Long id) {
        return repository.findById(id).get();
    }
}