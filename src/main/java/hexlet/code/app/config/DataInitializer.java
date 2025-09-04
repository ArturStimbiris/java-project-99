package hexlet.code.app.config;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
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
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            User user = new User();
            user.setEmail("hexlet@example.com");
            user.setPassword(passwordEncoder.encode("qwerty"));
            user.setFirstName("Hexlet");
            user.setLastName("User");
            userRepository.save(user);
        }

        // Добавляем дефолтные статусы
        addDefaultStatus("Draft", "draft");
        addDefaultStatus("ToReview", "to_review");
        addDefaultStatus("ToBeFixed", "to_be_fixed");
        addDefaultStatus("ToPublish", "to_publish");
        addDefaultStatus("Published", "published");
    }

    private void addDefaultStatus(String name, String slug) {
        if (taskStatusRepository.findBySlug(slug).isEmpty()) {
            TaskStatus status = new TaskStatus();
            status.setName(name);
            status.setSlug(slug);
            taskStatusRepository.save(status);
        }
    }
}
