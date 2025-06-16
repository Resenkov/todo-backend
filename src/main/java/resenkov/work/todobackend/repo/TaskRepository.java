package resenkov.work.todobackend.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import resenkov.work.todobackend.entity.Task;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {


    List<Task> findByUserEmailOrderByTitleAsc(String email);

    @Query("""
SELECT t
FROM Task t
    JOIN t.user u
WHERE 
    (:title IS NULL OR :title = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
    AND (:completed IS NULL OR t.completed = :completed)
    AND (:priorityId IS NULL OR t.priority.id = :priorityId)
    AND (:categoryId IS NULL OR t.category.id = :categoryId)
    AND (t.date IS NULL OR (t.date >= :dateFrom AND t.date <= :dateTo))
    AND u.email = :email
ORDER BY t.date DESC
""")
    Page<Task> findByParams(
            @Param("title") String title,
            @Param("completed") Boolean completed,
            @Param("priorityId") Long priorityId,
            @Param("categoryId") Long categoryId,
            @Param("email") String email,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable
    );
}