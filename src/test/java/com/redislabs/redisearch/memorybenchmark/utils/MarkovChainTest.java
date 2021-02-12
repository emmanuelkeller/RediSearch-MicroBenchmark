package com.redislabs.redisearch.memorybenchmark.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MarkovChainTest {

    @Test
    public void testGenerate100words() throws IOException {
        MarkovChain markovChain = MarkovChain.of();
        final String text100 = markovChain.generateText(100);
        assertNotNull(text100);
        assertEquals(100, text100.split(" ").length, 100);
    }

    @Test
    public void testGenerate2words() throws IOException {
        MarkovChain markovChain = MarkovChain.of();
        final String text2 = markovChain.generateText(2);
        assertNotNull(text2);
        assertEquals(2, text2.split(" ").length);
    }

    @Test
    public void testGeneratedTextIsRandom() throws IOException {
        MarkovChain markovChain = MarkovChain.of();
        final String text1 = markovChain.generateText(50);
        final String text2 = markovChain.generateText(50);
        assertNotEquals(text1, text2);
    }

    @Test
    public void testIllegalArgument() throws IOException {
        MarkovChain markovChain = MarkovChain.of();
        assertThrows(IllegalArgumentException.class, () -> markovChain.generateText(0));
    }
}
