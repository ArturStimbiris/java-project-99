package hexlet.code.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDTO {
    @Size(min = 1)
    private String title;

    private Integer index;

    private String content;

    private Long taskStatusId;

    private Long assigneeId;

    private Set<Long> labelIds;
}
