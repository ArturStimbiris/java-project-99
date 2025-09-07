package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.service.LabelService;
import hexlet.code.app.service.TaskStatusService;
import hexlet.code.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TaskMapper {

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private UserService userService;

    @Autowired
    private LabelService labelService;

    public TaskDTO map(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setIndex(task.getIndex());
        taskDTO.setCreatedAt(task.getCreatedAt());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setContent(task.getContent());
        taskDTO.setStatus(task.getTaskStatus().getSlug());
        if (task.getAssignee() != null) {
            taskDTO.setAssigneeId(task.getAssignee().getId());
        }
        if (task.getLabels() != null) {
            taskDTO.setLabelIds(task.getLabels().stream()
                    .map(label -> label.getId())
                    .collect(Collectors.toSet()));
        }
        return taskDTO;
    }

    public Task map(TaskCreateDTO taskCreateDTO) {
        Task task = new Task();
        task.setTitle(taskCreateDTO.getTitle());
        task.setIndex(taskCreateDTO.getIndex());
        task.setContent(taskCreateDTO.getContent());
        task.setTaskStatus(taskStatusService.findByIdEntity(taskCreateDTO.getTaskStatusId()));
        if (taskCreateDTO.getAssigneeId() != null) {
            task.setAssignee(userService.findByIdEntity(taskCreateDTO.getAssigneeId()));
        }
        if (taskCreateDTO.getLabelIds() != null) {
            task.setLabels(taskCreateDTO.getLabelIds().stream()
                    .map(labelId -> labelService.findByIdEntity(labelId))
                    .collect(Collectors.toSet()));
        }
        return task;
    }

    public void update(TaskUpdateDTO taskUpdateDTO, Task task) {
        if (taskUpdateDTO.getTitle() != null) {
            task.setTitle(taskUpdateDTO.getTitle());
        }
        if (taskUpdateDTO.getIndex() != null) {
            task.setIndex(taskUpdateDTO.getIndex());
        }
        if (taskUpdateDTO.getContent() != null) {
            task.setContent(taskUpdateDTO.getContent());
        }
        if (taskUpdateDTO.getTaskStatusId() != null) {
            task.setTaskStatus(taskStatusService.findByIdEntity(taskUpdateDTO.getTaskStatusId()));
        }
        if (taskUpdateDTO.getAssigneeId() != null) {
            task.setAssignee(userService.findByIdEntity(taskUpdateDTO.getAssigneeId()));
        }
        if (taskUpdateDTO.getLabelIds() != null) {
            task.setLabels(taskUpdateDTO.getLabelIds().stream()
                    .map(labelId -> labelService.findByIdEntity(labelId))
                    .collect(Collectors.toSet()));
        }
    }
}
