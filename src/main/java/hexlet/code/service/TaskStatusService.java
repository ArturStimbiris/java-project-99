package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.TaskStatusNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAll() {
        List<TaskStatus> taskStatuses = taskStatusRepository.findAll();
        return taskStatuses.stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatus taskStatus = taskStatusMapper.map(taskStatusCreateDTO);
        taskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO findById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new TaskStatusNotFoundException(id));
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatus findByIdEntity(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new TaskStatusNotFoundException(id));
    }

    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new TaskStatusNotFoundException(id));
        taskStatusMapper.update(taskStatusUpdateDTO, taskStatus);
        taskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public void delete(Long id) {
        long taskCount = taskRepository.countByTaskStatusId(id);
        if (taskCount > 0) {
            throw new RuntimeException("Cannot delete task status with associated tasks");
        }

        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new TaskStatusNotFoundException(id));

        taskStatusRepository.delete(taskStatus);
    }

    public TaskStatus findBySlug(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new TaskStatusNotFoundException(slug));
    }
}
