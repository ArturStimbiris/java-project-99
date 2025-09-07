package hexlet.code.app.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    @Size(min = 1)
    private String title;

    private Integer index;

    private String content;

    private Long taskStatusId;

    private Long assigneeId;
}
