package hexlet.code.app.mapper;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {

    public LabelDTO map(Label label) {
        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setId(label.getId());
        labelDTO.setName(label.getName());
        labelDTO.setCreatedAt(label.getCreatedAt());
        return labelDTO;
    }

    public Label map(LabelCreateDTO labelCreateDTO) {
        Label label = new Label();
        label.setName(labelCreateDTO.getName());
        return label;
    }

    public void update(LabelUpdateDTO labelUpdateDTO, Label label) {
        if (labelUpdateDTO.getName() != null) {
            label.setName(labelUpdateDTO.getName());
        }
    }
}
