package hexlet.code.app.controller;

import hexlet.code.app.model.User;
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
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    private String getToken(String email, String password) throws Exception {
        String credentials = "{\"username\":\"" + email + "\",\"password\":\"" + password + "\"}";
        MvcResult result = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("token").asText();
    }

    @BeforeEach
    void setUp() throws Exception {
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
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testShow() throws Exception {
        User user = userRepository.findByEmail("admin@example.com").get();

        mockMvc.perform(get("/api/users/{id}", user.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"));
    }

    @Test
    void testCreate() throws Exception {
        String userData
            = "{\"email\":\"test@example.com\",\"password\":\"password\",\"firstName\":\"Test\",\"lastName\":\"User\"}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("test@example.com").get();
        assertThat(user).isNotNull();
    }

    @Test
    void testUpdate() throws Exception {
        User user = userRepository.findByEmail("admin@example.com").get();
        String updateData = "{\"email\":\"new@example.com\",\"firstName\":\"New\",\"lastName\":\"Name\"}";

        mockMvc.perform(put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateData)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).get();
        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void testDestroy() throws Exception {
        User user = userRepository.findByEmail("admin@example.com").get();

        mockMvc.perform(delete("/api/users/{id}", user.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(user.getId())).isFalse();
    }
}
