package com.redislabs.redisearch.memorybenchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BenchmarkSchemaTest {

    public static BenchmarkSchema getSchema(String resourceName) {
        try (final InputStream is = BenchmarkSchema.class.getResourceAsStream(resourceName)) {
            return new ObjectMapper().readValue(is, BenchmarkSchema.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test() {
        assertNotNull(getSchema("schema.json"));
    }

}
