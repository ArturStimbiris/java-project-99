package hexlet.code.controller;

import hexlet.code.AppApplication;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.model.Label;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;
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
    private LabelRepository labelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;
    private User testUser;
    private TaskStatus testStatus;
    private Label testLabel;

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
        labelRepository.deleteAll();

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

        // Создаем метку
        testLabel = new Label();
        testLabel.setName("Test Label");
        labelRepository.save(testLabel);

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

    @Test
    void testFilterByTitle() throws Exception {
        Task task1 = new Task();
        task1.setTitle("Create new feature");
        task1.setContent("Test Content");
        task1.setTaskStatus(testStatus);
        task1.setAssignee(testUser);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Fix bug");
        task2.setContent("Test Content");
        task2.setTaskStatus(testStatus);
        task2.setAssignee(testUser);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks?titleCont=feature")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Create new feature"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void testFilterByAssignee() throws Exception {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setContent("Test Content");
        task1.setTaskStatus(testStatus);
        task1.setAssignee(testUser);
        taskRepository.save(task1);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword(passwordEncoder.encode("password"));
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        userRepository.save(anotherUser);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setContent("Test Content");
        task2.setTaskStatus(testStatus);
        task2.setAssignee(anotherUser);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks?assigneeId=" + testUser.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void testFilterByStatus() throws Exception {
        TaskStatus anotherStatus = new TaskStatus();
        anotherStatus.setName("Another Status");
        anotherStatus.setSlug("another_status");
        taskStatusRepository.save(anotherStatus);

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setContent("Test Content");
        task1.setTaskStatus(testStatus);
        task1.setAssignee(testUser);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setContent("Test Content");
        task2.setTaskStatus(anotherStatus);
        task2.setAssignee(testUser);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks?status=" + testStatus.getSlug())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void testFilterByLabel() throws Exception {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setContent("Test Content");
        task1.setTaskStatus(testStatus);
        task1.setAssignee(testUser);
        task1.getLabels().add(testLabel);
        taskRepository.save(task1);

        Label anotherLabel = new Label();
        anotherLabel.setName("Another Label");
        labelRepository.save(anotherLabel);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setContent("Test Content");
        task2.setTaskStatus(testStatus);
        task2.setAssignee(testUser);
        task2.getLabels().add(anotherLabel);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks?labelId=" + testLabel.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(1)));
    }
}
