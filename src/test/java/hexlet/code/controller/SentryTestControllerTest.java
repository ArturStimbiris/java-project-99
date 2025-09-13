package hexlet.code.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class SentryTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSentryTest() throws Exception {
        mockMvc.perform(get("/sentry-test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Exception caught and sent to Sentry! Check your Sentry dashboard."));
    }

    @Test
    void testSentryTest2() throws Exception {
        mockMvc.perform(get("/sentry-test-2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Array index error sent to Sentry! Check your Sentry dashboard."));
    }
}
