package hexlet.code.app.controller;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    private String getToken(String email, String password) throws Exception {
        String credentials = "{\"username\":\"" + email + "\",\"password\":\"" + password + "\"}";
        var result = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("token").asText();
    }

    @BeforeEach
    void setUp() throws Exception {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("admin@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Admin");
        user.setLastName("User");
        userRepository.save(user);

        token = getToken("admin@example.com", "password");
    }

    @Test
    void testIndex() throws Exception {
        TaskStatus status = new TaskStatus();
        status.setName("Test Status");
        status.setSlug("test_status");
        taskStatusRepository.save(status);

        mockMvc.perform(get("/api/task_statuses")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Status"));
    }

    @Test
    void testShow() throws Exception {
        TaskStatus status = new TaskStatus();
        status.setName("Test Status");
        status.setSlug("test_status");
        taskStatusRepository.save(status);

        mockMvc.perform(get("/api/task_statuses/{id}", status.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Status"));
    }

    @Test
    void testCreate() throws Exception {
        String statusData = "{\"name\":\"New Status\",\"slug\":\"new_status\"}";

        mockMvc.perform(post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusData)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        TaskStatus status = taskStatusRepository.findBySlug("new_status").orElse(null);
        assertThat(status).isNotNull();
        assertThat(status.getName()).isEqualTo("New Status");
    }

    @Test
    void testUpdate() throws Exception {
        TaskStatus status = new TaskStatus();
        status.setName("Old Status");
        status.setSlug("old_status");
        taskStatusRepository.save(status);

        String updateData = "{\"name\":\"Updated Status\"}";

        mockMvc.perform(put("/api/task_statuses/{id}", status.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateData)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        TaskStatus updatedStatus = taskStatusRepository.findById(status.getId()).orElse(null);
        assertThat(updatedStatus).isNotNull();
        assertThat(updatedStatus.getName()).isEqualTo("Updated Status");
    }

    @Test
    void testDestroy() throws Exception {
        TaskStatus status = new TaskStatus();
        status.setName("Test Status");
        status.setSlug("test_status");
        taskStatusRepository.save(status);

        mockMvc.perform(delete("/api/task_statuses/{id}", status.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.existsById(status.getId())).isFalse();
    }
}
