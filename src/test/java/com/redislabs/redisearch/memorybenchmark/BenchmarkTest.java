package com.redislabs.redisearch.memorybenchmark;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BenchmarkTest {

    @Test
    public void test() throws IOException {
        try (final Benchmark benchmark = new Benchmark(SchemaTest.getSchema())) {
            benchmark.execute();
        }
    }
}
