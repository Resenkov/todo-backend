package resenkov.work.todobackend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import resenkov.work.todobackend.entity.Categories;

import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Categories, Long> {
    List<Categories> findByUserEmailOrderByTitleAsc(String email);

    @Query("SELECT c FROM Categories c where " +
            "(:title is null or :title =' ' " +
            " or lower(c.title) like lower(concat('%', :title,'%'))) " +
            " and c.user.email=:email " +
            "order by c.title asc")
    List<Categories> findByTitle(@Param("title") String title, @Param("email") String email);

}
// Вы можете уже сразу использовать все методы CRUD (Create, Read, Update, Delete)
// принцип ООП: абстракция-реализация - здесь описываем все доступные способы доступа к данным
