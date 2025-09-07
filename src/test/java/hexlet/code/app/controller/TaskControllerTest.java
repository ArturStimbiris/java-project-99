package hexlet.code.app.controller;

import hexlet.code.app.AppApplication;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = AppApplication.class)
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;
    private User testUser;
    private TaskStatus testStatus;

    private String getToken(String email, String password) throws Exception {
        String credentials = "{\"username\":\"" + email + "\",\"password\":\"" + password + "\"}";
        MvcResult result = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    @BeforeEach
    void setUp() throws Exception {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        // Создаем пользователя
        testUser = new User();
        testUser.setEmail("admin@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setFirstName("Admin");
        testUser.setLastName("User");
        userRepository.save(testUser);

        // Создаем статус
        testStatus = new TaskStatus();
        testStatus.setName("Test Status");
        testStatus.setSlug("test_status");
        taskStatusRepository.save(testStatus);

        token = getToken("admin@example.com", "password");
    }

    @Test
    void testIndex() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setContent("Test Content");
        task.setTaskStatus(testStatus);
        task.setAssignee(testUser);
        taskRepository.save(task);

        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void testShow() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setContent("Test Content");
        task.setTaskStatus(testStatus);
        task.setAssignee(testUser);
        taskRepository.save(task);

        mockMvc.perform(get("/api/tasks/{id}", task.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void testCreate() throws Exception {
        String taskData = "{\"title\":\"New Task\",\"content\":\"New Content\",\"taskStatusId\":"
        + testStatus.getId() + ",\"assigneeId\":" + testUser.getId() + "}";

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskData)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        Task task = taskRepository.findAll().get(0);
        assertThat(task).isNotNull();
        assertThat(task.getTitle()).isEqualTo("New Task");
    }

    @Test
    void testUpdate() throws Exception {
        Task task = new Task();
        task.setTitle("Old Task");
        task.setContent("Old Content");
        task.setTaskStatus(testStatus);
        task.setAssignee(testUser);
        taskRepository.save(task);

        String updateData = "{\"title\":\"Updated Task\"}";

        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateData)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Task updatedTask = taskRepository.findById(task.getId()).orElse(null);
        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getTitle()).isEqualTo("Updated Task");
    }

    @Test
    void testDestroy() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setContent("Test Content");
        task.setTaskStatus(testStatus);
        task.setAssignee(testUser);
        taskRepository.save(task);

        mockMvc.perform(delete("/api/tasks/{id}", task.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(task.getId())).isFalse();
    }
}
