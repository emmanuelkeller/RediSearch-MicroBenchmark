package com.redislabs.redisearch.memorybenchmark;

import com.redislabs.redisearch.memorybenchmark.utils.RedisUtils;
import io.redisearch.Schema;
import io.redisearch.client.Client;
import io.redisearch.client.IndexDefinition;
import redis.clients.jedis.Jedis;

import java.io.Closeable;
import java.util.Map;
import java.util.Objects;

public class BenchmarkClient implements Closeable {

    private final BenchmarkSchema.Endpoint endpoint;
    private final String indexName;

    private final Jedis jedisClient;
    private final Client jedisSearchClient;

    private final BenchmarkResult.Builder resultBuilder;

    public BenchmarkClient(BenchmarkSchema.Endpoint endpoint, String indexName) {
        this.endpoint = endpoint;
        this.indexName = indexName;
        this.jedisClient = new Jedis(endpoint.hostname, endpoint.port);
        this.jedisSearchClient = new Client(indexName, endpoint.hostname, endpoint.port);
        this.resultBuilder = BenchmarkResult.of();
    }

    public void flushEverything() {
        jedisSearchClient.dropIndex(true);
        jedisClient.flushAll();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public BenchmarkResult.Builder getResultBuilder() {
        return resultBuilder;
    }

    public long collectUsedMemory() {
        return Long.parseLong(RedisUtils.parseInfo(jedisClient.info("memory")).get("used_memory"));
    }

    public void hset(String key, Map<String, String> hash) {
        jedisClient.hset(key, hash);
    }

    public void createIndex(String docPrefix, Schema schema) {
        final IndexDefinition indexDefinition = new IndexDefinition();
        indexDefinition.setPrefixes(docPrefix);
        jedisSearchClient.createIndex(schema, Client.IndexOptions.defaultOptions().setDefinition(indexDefinition));

    }

    public void waitForIndexing(int numDocs) {
        for (; ; ) {
            final Map<String, Object> infos = jedisSearchClient.getInfo();
            final int indexing = Integer.parseInt(infos.get("indexing").toString());
            if (indexing == 0) {
                final int hashIndexingFailures = Integer.parseInt(infos.get("hash_indexing_failures").toString());
                final int numRecords = Integer.parseInt(infos.get("num_docs").toString());
                if (numRecords != numDocs)
                    throw new RuntimeException("Wrong number of records: " + numRecords);
                if (hashIndexingFailures > 0)
                    throw new RuntimeException("Hash indexing failures: " + hashIndexingFailures);
                return;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint.hostname, endpoint.port, indexName);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BenchmarkClient))
            return false;
        final BenchmarkClient o = (BenchmarkClient) other;
        return Objects.equals(endpoint.hostname, o.endpoint.hostname)
                && Objects.equals(endpoint.port, o.endpoint.port)
                && Objects.equals(indexName, o.indexName);
    }

    @Override
    public void close() {
        jedisClient.close();
        jedisSearchClient.close();
    }

}
