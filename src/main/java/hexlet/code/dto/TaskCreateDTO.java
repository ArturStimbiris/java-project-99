package hexlet.code.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
    @Size(min = 1)
    private String title;

    private Integer index;

    private String content;

    private String status;

    private Long assigneeId;

    private Set<Long> taskLabelIds;
}
