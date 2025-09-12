package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.TaskNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public List<TaskDTO> getAll() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(taskMapper::map)
                .toList();
    }

    public List<TaskDTO> getWithFilters(String titleCont, Long assigneeId, String status, Long labelId) {
        List<Task> tasks = taskRepository.findByFilters(assigneeId, status, labelId);

        if (titleCont != null && !titleCont.isEmpty()) {
            String finalTitleCont = titleCont.toLowerCase();
            tasks = tasks.stream()
                    .filter(task -> task.getTitle().toLowerCase().contains(finalTitleCont))
                    .collect(Collectors.toList());
        }

        return tasks.stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO create(TaskCreateDTO taskCreateDTO) {
        Task task = taskMapper.map(taskCreateDTO);
        task = taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.map(task);
    }

    public TaskDTO update(Long id, TaskUpdateDTO taskUpdateDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskMapper.update(taskUpdateDTO, task);
        task = taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }
}
