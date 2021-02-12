package com.redislabs.redisearch.memorybenchmark.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Inspired by
 * https://rosettacode.org/wiki/Markov_chain_text_generator#Java
 */
public class MarkovChain {

    private final Random rnd = new Random();
    private final Map<String, List<String>> dict = new HashMap<>();
    private final String[] words;
    private final int keySize;

    private MarkovChain(final String textSource, int keySize) {
        if (keySize < 1) throw new IllegalArgumentException("Key size can't be less than 1");
        this.keySize = keySize;
        words = textSource.trim().split("\\W+");
        for (int i = 0; i < (words.length - keySize); ++i) {
            StringBuilder key = new StringBuilder(words[i]);
            for (int j = i + 1; j < i + keySize; ++j) {
                key.append(' ').append(words[j]);
            }
            String value = (i + keySize < words.length) ? words[i + keySize] : "";
            if (!dict.containsKey(key.toString())) {
                List<String> list = new ArrayList<>();
                list.add(value);
                dict.put(key.toString(), list);
            } else {
                dict.get(key.toString()).add(value);
            }
        }
    }

    public static MarkovChain of() throws IOException {
        try (final InputStream inputStream = MarkovChain.class.getResourceAsStream("alice_oz.txt")) {
            byte[] bytes = inputStream.readAllBytes();
            return new MarkovChain(new String(bytes, StandardCharsets.US_ASCII), 1);
        }
    }

    public String generateText(int outputSize) {
        if (outputSize < keySize || outputSize >= words.length) {
            throw new IllegalArgumentException("Output size is out of range: " + outputSize);
        }
        int n = 0;
        int rn = rnd.nextInt(dict.size());
        String prefix = (String) dict.keySet().toArray()[rn];
        final List<String> output = new ArrayList<>(Arrays.asList(prefix.split(" ")));

        while (true) {
            final List<String> suffix = dict.get(prefix);
            if (suffix.size() == 1) {
                if (Objects.equals(suffix.get(0), ""))
                    return output.stream().reduce("", (a, b) -> a + " " + b).trim();
                output.add(suffix.get(0));
            } else {
                rn = rnd.nextInt(suffix.size());
                output.add(suffix.get(rn));
            }
            if (output.size() >= outputSize)
                return output.stream().limit(outputSize).reduce("", (a, b) -> a + " " + b).trim();
            n++;
            prefix = output.stream().skip(n).limit(keySize).reduce("", (a, b) -> a + " " + b).trim();
        }
    }
}
