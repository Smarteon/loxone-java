package cz.smarteon.loxone.message

import com.fasterxml.jackson.databind.ObjectMapper

import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES

trait SerializationSupport {
    static ObjectMapper MAPPER = new ObjectMapper().configure(ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

    static <T> T readResource(String path, Class<T> type) {
        return MAPPER.readValue(getClass().getResourceAsStream(path), type)
    }
}
