package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDTO> getAll() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(taskMapper::map)
                .toList();
    }

    public List<TaskDTO> getWithFilters(String titleCont, Long assigneeId, String status, Long labelId) {
        List<Task> tasks = taskRepository.findByFilters(titleCont, assigneeId, status, labelId);
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
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return taskMapper.map(task);
    }

    public TaskDTO update(Long id, TaskUpdateDTO taskUpdateDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskMapper.update(taskUpdateDTO, task);
        task = taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    public Task findByIdEntity(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
}
