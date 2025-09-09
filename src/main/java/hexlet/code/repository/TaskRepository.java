package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByTaskStatusId(Long taskStatusId);
    long countByAssigneeId(Long assigneeId);
    long countByTaskStatusId(Long taskStatusId);

    @Query("SELECT t FROM Task t WHERE "
            + "(:titleCont IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :titleCont, '%'))) AND "
            + "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND "
            + "(:status IS NULL OR t.taskStatus.slug = :status) AND "
            + "(:labelId IS NULL OR :labelId IN (SELECT l.id FROM t.labels l))")
    List<Task> findByFilters(
            @Param("titleCont") String titleCont,
            @Param("assigneeId") Long assigneeId,
            @Param("status") String status,
            @Param("labelId") Long labelId
    );
}
