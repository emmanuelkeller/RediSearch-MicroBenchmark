package com.redislabs.redisearch.memorybenchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SchemaTest {

    public static Schema getSchema() throws IOException {
        try (final InputStream is = SchemaTest.class.getResourceAsStream("schema.json")) {
            return new ObjectMapper().readValue(is, Schema.class);
        }
    }

    @Test
    public void test() throws IOException {
        assertNotNull(getSchema());
    }

}
