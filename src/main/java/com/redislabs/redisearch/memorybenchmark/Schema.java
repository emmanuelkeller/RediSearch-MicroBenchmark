package com.redislabs.redisearch.memorybenchmark;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.LinkedHashMap;
import java.util.List;

public class Schema {

    public static class Endpoint {

        @JsonProperty("hostname")
        public final String hostname;

        @JsonProperty("port")
        public final Integer port;

        @JsonCreator
        public Endpoint(@JsonProperty("hostname") String hostname,
                        @JsonProperty("port") Integer port) {
            this.hostname = hostname;
            this.port = port;
        }
    }

    @JsonProperty("fields")
    public final LinkedHashMap<String, Field> fields;

    @JsonProperty("number_of_documents")
    public final Integer numberOfDocuments;

    @JsonProperty("redis_enpoints")
    public final List<Endpoint> redisEndpoints;

    @JsonCreator
    public Schema(@JsonProperty("fields") LinkedHashMap<String, Field> fields,
                  @JsonProperty("number_of_documents") Integer numberOfDocuments,
                  @JsonProperty("redis_enpoints") List<Endpoint> redisEndpoints) {
        this.fields = fields;
        this.numberOfDocuments = numberOfDocuments;
        this.redisEndpoints = redisEndpoints;
    }

}
