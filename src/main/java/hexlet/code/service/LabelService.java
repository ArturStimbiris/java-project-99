package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.LabelNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> getAll() {
        List<Label> labels = labelRepository.findAll();
        return labels.stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO create(LabelCreateDTO labelCreateDTO) {
        Label label = labelMapper.map(labelCreateDTO);
        label = labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO findById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException(id));
        return labelMapper.map(label);
    }

    public Label findByIdEntity(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException(id));
    }

    public LabelDTO update(Long id, LabelUpdateDTO labelUpdateDTO) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException(id));
        labelMapper.update(labelUpdateDTO, label);
        label = labelRepository.save(label);
        return labelMapper.map(label);
    }

    public void delete(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException(id));

        if (!label.getTasks().isEmpty()) {
            throw new RuntimeException("Cannot delete label with associated tasks");
        }

        labelRepository.delete(label);
    }

    public Label findByName(String name) {
        return labelRepository.findByName(name)
                .orElseThrow(() -> new LabelNotFoundException(name));
    }
}
