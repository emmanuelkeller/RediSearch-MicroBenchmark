package com.redislabs.redisearch.memorybenchmark;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BenchmarkTest {

    @Test
    public void test() {
        try (final Benchmark benchmark = new Benchmark(BenchmarkSchemaTest.getSchema("schema.json"))) {
            benchmark.execute();
        }
    }

    @Test
    public void test_suite() throws IOException {
        Files.list(
                Path.of("src", "test", "resources", "com", "redislabs", "redisearch", "memorybenchmark")
        ).forEach(path -> {
            final String jsonFile = path.getFileName().toString();
            System.out.println("Testing: " + jsonFile);
            try (final Benchmark benchmark = new Benchmark(BenchmarkSchemaTest.getSchema(jsonFile))) {
                benchmark.execute();
            }
        });
    }
}
