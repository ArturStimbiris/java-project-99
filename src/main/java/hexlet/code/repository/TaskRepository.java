package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE "
            + "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND "
            + "(:status IS NULL OR t.taskStatus.slug = :status) AND "
            + "(:labelId IS NULL OR EXISTS (SELECT l FROM t.labels l WHERE l.id = :labelId)) AND "
            + "(:titleCont IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', CAST(:titleCont AS text), '%')))")
    List<Task> findByFilters(
            @Param("assigneeId") Long assigneeId,
            @Param("status") String status,
            @Param("labelId") Long labelId,
            @Param("titleCont") String titleCont
    );
}
