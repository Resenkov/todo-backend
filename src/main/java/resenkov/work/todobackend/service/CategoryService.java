package resenkov.work.todobackend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import resenkov.work.todobackend.entity.Categories;
import resenkov.work.todobackend.repo.CategoryRepository;

import java.util.List;

// всегда нужно создавать отдельный класс Service для доступа к данным, даже если кажется,
// что мало методов или это все можно реализовать сразу в контроллере
// Такой подход полезен для будущих доработок и правильной архитектуры (особенно, если работаете с транзакциями)
@Service

// все методы класса должны выполниться без ошибки, чтобы транзакция завершилась
// если в методе выполняются несолько SQL запросов и возникнет исключение - то все выполненные операции откатятся (Rollback)
@Transactional
public class CategoryService {

    // работает встроенный механизм DI из Spring, который при старте приложения подставит в эту переменную нужные класс-реализацию
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