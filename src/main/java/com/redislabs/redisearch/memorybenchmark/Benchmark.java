package com.redislabs.redisearch.memorybenchmark;

import io.redisearch.client.Client;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Benchmark implements Closeable {

    private static final String INDEX_NAME = "benchmark";

    private final FieldValueGenerator fieldValueGenerator;
    private final Schema schema;
    private final List<Jedis> jedisClients;
    private final List<Client> jedisSearchClients;

    public Benchmark(Schema schema) throws IOException {
        this.fieldValueGenerator = new FieldValueGenerator(schema);
        this.schema = schema;
        jedisClients = new ArrayList<>(schema.redisEndpoints.size());
        jedisSearchClients = new ArrayList<>(schema.redisEndpoints.size());
        for (Schema.Endpoint endpoint : schema.redisEndpoints) {
            jedisClients.add(new Jedis(endpoint.hostname, endpoint.port));
            jedisSearchClients.add(new Client(INDEX_NAME, endpoint.hostname, endpoint.port));
        }
    }

    public void execute() {
        jedisClients.forEach(BinaryJedis::flushDB);
        createHashes();
    }

    private Map<String, String> getNewHash() {
        final Map<String, String> hash = new HashMap<>();
        schema.fields.forEach((name, field) -> {
            hash.put(name, fieldValueGenerator.generateValue(name));
        });
        return hash;
    }

    public void createHashes() {
        for (int i = 1; i <= schema.numberOfDocuments; i++) {
            final String key = "doc:" + i;
            for (Jedis client : jedisClients) {
                client.hset(key, getNewHash());
            }
        }
    }


    @Override
    public void close() {
        jedisSearchClients.forEach(Client::close);
        jedisClients.forEach(Jedis::close);
    }
}
