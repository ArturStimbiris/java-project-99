package hexlet.code.config;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.model.Label;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = null;
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            user = new User();
            user.setEmail("hexlet@example.com");
            user.setPassword(passwordEncoder.encode("qwerty"));
            user.setFirstName("Hexlet");
            user.setLastName("User");
            user = userRepository.save(user);
        } else {
            user = userRepository.findByEmail("hexlet@example.com").get();
        }

        TaskStatus draft = addDefaultStatus("Draft", "draft");
        TaskStatus toReview = addDefaultStatus("ToReview", "to_review");
        TaskStatus toBeFixed = addDefaultStatus("ToBeFixed", "to_be_fixed");
        TaskStatus toPublish = addDefaultStatus("ToPublish", "to_publish");
        TaskStatus published = addDefaultStatus("Published", "published");

        Label bug = addDefaultLabel("bug");
        Label feature = addDefaultLabel("feature");

        if (taskRepository.count() == 0) {
            Task task1 = new Task();
            task1.setTitle("First task");
            task1.setIndex(1);
            task1.setContent("This is the first task");
            task1.setTaskStatus(draft);
            task1.setAssignee(user);
            taskRepository.save(task1);

            Task task2 = new Task();
            task2.setTitle("Second task");
            task2.setIndex(2);
            task2.setContent("This is the second task");
            task2.setTaskStatus(toReview);
            task2.setAssignee(user);
            taskRepository.save(task2);
        }
    }

    private TaskStatus addDefaultStatus(String name, String slug) {
        return taskStatusRepository.findBySlug(slug).orElseGet(() -> {
            TaskStatus status = new TaskStatus();
            status.setName(name);
            status.setSlug(slug);
            return taskStatusRepository.save(status);
        });
    }

    private Label addDefaultLabel(String name) {
        return labelRepository.findByName(name).orElseGet(() -> {
            Label label = new Label();
            label.setName(name);
            return labelRepository.save(label);
        });
    }
}
