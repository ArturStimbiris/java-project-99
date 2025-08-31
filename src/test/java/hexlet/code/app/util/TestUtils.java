package hexlet.code.app.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String toJson(Object obj) throws Exception {
        return MAPPER.writeValueAsString(obj);
    }
}
