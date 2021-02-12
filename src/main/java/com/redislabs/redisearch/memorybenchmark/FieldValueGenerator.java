package com.redislabs.redisearch.memorybenchmark;

import com.redislabs.redisearch.memorybenchmark.utils.MarkovChain;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FieldValueGenerator {

    private final Random random;
    private final MarkovChain markovChain;
    private final Map<String, Supplier<String>> fieldGenerators;

    public FieldValueGenerator(Schema schema) throws IOException {
        random = new Random();
        markovChain = MarkovChain.of();
        fieldGenerators = new HashMap<>();
        schema.fields.forEach((name, field) -> {
            fieldGenerators.put(name, field.createSupplier(this));
        });
    }

    String generateText(int numWords) {
        return markovChain.generateText(numWords);
    }

    int nextRandomInt(int fromInclusive, int toInclusive) {
        if (fromInclusive == toInclusive)
            return fromInclusive;
        return random.nextInt(1 + toInclusive - fromInclusive) + fromInclusive;
    }

    int nextRandomInt(int bound) {
        return random.nextInt(bound);
    }

    String generateValue(final String field) {
        return Objects.requireNonNull(fieldGenerators.get(field), () -> "Field not found: " + field).get();
    }

    List<String> generateDict(int minWords, int maxWords, int cardinality) {
        final Set<String> words = new HashSet<>();
        while (words.size() < cardinality) {
            words.add(generateText(nextRandomInt(minWords, maxWords)));
        }
        return new ArrayList<>(words);
    }
}

