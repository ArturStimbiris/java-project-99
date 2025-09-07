package hexlet.code.app.repository;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByTaskStatusId(Long taskStatusId);
    long countByAssigneeId(Long assigneeId);
    long countByTaskStatusId(Long taskStatusId);
}
