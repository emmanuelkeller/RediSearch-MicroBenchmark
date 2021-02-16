package com.redislabs.redisearch.memorybenchmark;

public class BenchmarkResult {

    public final int numDoc;

    public final long initialMemory;
    public final long postHashMemory;
    public final long postIndexMemory;

    public final long hashMemory;
    public final long indexMemory;
    public final long totalMemory;

    public final long perDocHashMemory;
    public final long perDocIndexMemory;
    public final long perDocTotalMemory;

    private BenchmarkResult(Builder builder) {
        this.numDoc = builder.numDoc;
        this.initialMemory = builder.initialMemory;
        this.postHashMemory = builder.postHashMemory;
        this.postIndexMemory = builder.postIndexMemory;
        hashMemory = postHashMemory - initialMemory;
        indexMemory = postIndexMemory - postHashMemory;
        totalMemory = hashMemory + indexMemory;
        perDocHashMemory = hashMemory / numDoc;
        perDocIndexMemory = indexMemory / numDoc;
        perDocTotalMemory = perDocHashMemory + perDocIndexMemory;
    }

    public static Builder of() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "hash: " + perDocHashMemory + " - index: " + perDocIndexMemory + " - total: " + perDocTotalMemory;
    }

    public static class Builder {

        private int numDoc;
        private long initialMemory;
        private long postHashMemory;
        private long postIndexMemory;

        public Builder numDoc(int numDoc) {
            this.numDoc = numDoc;
            return this;
        }

        public Builder initialMemory(long memory) {
            this.initialMemory = memory;
            return this;
        }

        public Builder postHashMemory(long memory) {
            this.postHashMemory = memory;
            return this;
        }

        public Builder postIndexMemory(long memory) {
            this.postIndexMemory = memory;
            return this;
        }

        public BenchmarkResult build() {
            return new BenchmarkResult(this);
        }

    }
}
