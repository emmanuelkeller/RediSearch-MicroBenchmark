package com.redislabs.redisearch.memorybenchmark;

import io.redisearch.Schema;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class Benchmark implements Closeable {

    public static final String DOC_PREFIX = "d:";
    public static final String INDEX_NAME = "benchmark";

    private final FieldValueGenerator fieldValueGenerator;
    private final BenchmarkSchema benchmarkSchema;
    private final List<BenchmarkClient> clients;

    public Benchmark(BenchmarkSchema benchmarkSchema) {
        this.fieldValueGenerator = new FieldValueGenerator(benchmarkSchema);
        this.benchmarkSchema = benchmarkSchema;
        clients = new ArrayList<>(benchmarkSchema.redisEndpoints.size());
        for (BenchmarkSchema.Endpoint endpoint : benchmarkSchema.redisEndpoints) {
            clients.add(new BenchmarkClient(endpoint, INDEX_NAME));
        }
    }

    public void execute() {
        init();
        createHashes();
        createSchema();
        publishResults();
    }

    private Map<String, String> getNewHash() {
        final Map<String, String> hash = new HashMap<>();
        benchmarkSchema.fields.forEach((name, field) -> {
            hash.put(name, fieldValueGenerator.generateValue(name));
        });
        return hash;
    }

    private void init() {
        clients.forEach(client -> {
            client.flushEverything();
            client.getResultBuilder()
                    .initialMemory(client.collectUsedMemory())
                    .numDoc(benchmarkSchema.numberOfDocuments);
        });
    }

    private void createHashes() {
        for (int i = 1; i <= benchmarkSchema.numberOfDocuments; i++) {
            final String key = DOC_PREFIX + i;
            for (BenchmarkClient client : clients) {
                client.hset(key, getNewHash());
                client.getResultBuilder().postHashMemory(client.collectUsedMemory());
            }
        }
    }

    private void createSchema() {
        final Schema schema = new Schema();
        benchmarkSchema.fields.forEach((name, options) -> options.applySchema(name, schema));
        clients.forEach(client -> client.createIndex(DOC_PREFIX, schema));
        clients.forEach(client -> {
            client.waitForIndexing(benchmarkSchema.numberOfDocuments);
            client.getResultBuilder().postIndexMemory(client.collectUsedMemory());
        });
    }

    public void publishResults() {
        clients.forEach(client -> {
            System.out.println(client.getResultBuilder().build().toString());
        });
    }

    @Override
    public void close() {
        clients.forEach(BenchmarkClient::close);
    }
}
