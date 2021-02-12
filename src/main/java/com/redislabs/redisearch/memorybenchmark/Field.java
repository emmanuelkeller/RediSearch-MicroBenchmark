package com.redislabs.redisearch.memorybenchmark;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.redislabs.redisearch.memorybenchmark.utils.MarkovChain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Field.TextField.class, name = "text"),
        @JsonSubTypes.Type(value = Field.TagField.class, name = "tag"),
        @JsonSubTypes.Type(value = Field.NumberField.class, name = "number")
})
public interface Field {

    Supplier<String> createSupplier(FieldValueGenerator fieldValueGenerator);

    abstract class AbstractField implements Field {

        @JsonProperty("sortable")
        public Boolean sortable;

        protected AbstractField(Boolean sortable) {
            this.sortable = sortable;
        }
    }

    abstract class AbstractTextField extends AbstractField {

        @JsonProperty("min_words")
        public final Integer minWords;

        @JsonProperty("max_words")
        public final Integer maxWords;

        public AbstractTextField(Boolean sortable,
                                 Integer minWords,
                                 Integer maxWords) {
            super(sortable);
            this.minWords = minWords;
            this.maxWords = maxWords;
        }
    }

    class TextField extends AbstractTextField {

        @JsonProperty("nostem")
        public final Boolean nostem;

        public TextField(@JsonProperty("sortable") boolean sortable,
                         @JsonProperty("min_words") int minWords,
                         @JsonProperty("max_words") int maxWords,
                         @JsonProperty("nostem") boolean nostem) {
            super(sortable, minWords, maxWords);
            this.nostem = nostem;
        }

        @Override
        public Supplier<String> createSupplier(FieldValueGenerator fieldValueGenerator) {
            return () -> fieldValueGenerator.generateText(fieldValueGenerator.nextRandomInt(minWords, maxWords));
        }

    }

    class TagField extends AbstractTextField {

        @JsonProperty("cardinality")
        public final Integer cardinality;

        @JsonProperty("min_tags")
        public final Integer minTags;

        @JsonProperty("max_tags")
        public final Integer maxTags;


        public TagField(@JsonProperty("sortable") Boolean sortable,
                        @JsonProperty("min_words") Integer minWords,
                        @JsonProperty("max_words") Integer maxWords,
                        @JsonProperty("cardinality") Integer cardinality,
                        @JsonProperty("min_tags") Integer minTags,
                        @JsonProperty("max-tags") Integer maxTags) {
            super(sortable, minWords, maxWords);
            this.cardinality = cardinality;
            this.minTags = minTags;
            this.maxTags = maxTags;
        }

        @Override
        public Supplier<String> createSupplier(FieldValueGenerator fieldValueGenerator) {
            final List<String> dict = fieldValueGenerator.generateDict(minWords, maxWords, cardinality);
            return () -> {
                int tagsNum = fieldValueGenerator.nextRandomInt(minTags, maxTags);
                final Set<String> tags = new LinkedHashSet<>(tagsNum);
                while (tags.size() < tagsNum) {
                    tags.add(dict.get(fieldValueGenerator.nextRandomInt(cardinality)));
                }
                return String.join(",", tags);
            };
        }
    }

    class NumberField extends AbstractField {

        @JsonProperty("cardinality")
        public final Integer cardinality;

        @JsonProperty("decimals")
        public final Integer decimals;

        @JsonProperty("min_value")
        public final Number minValue;

        @JsonProperty("max_value")
        public final Number maxValue;

        public NumberField(@JsonProperty("sortable") Boolean sortable,
                           @JsonProperty("cardinality") Integer cardinality,
                           @JsonProperty("decimals") Integer decimals,
                           @JsonProperty("min_value") Number minValue,
                           @JsonProperty("max_value") Number maxValue) {
            super(sortable);
            this.cardinality = cardinality;
            this.decimals = decimals;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public Supplier<String> createSupplier(FieldValueGenerator fieldValueGenerator) {
            final double step = (maxValue.doubleValue() - minValue.doubleValue()) / cardinality.doubleValue();
            final NumberFormat nf = DecimalFormat.getNumberInstance();
            nf.setMinimumFractionDigits(decimals == null ? 0 : decimals);
            nf.setMaximumFractionDigits(decimals == null ? 0 : decimals);
            return () -> {
                int weight = fieldValueGenerator.nextRandomInt(cardinality);
                return nf.format(step * weight);
            };
        }
    }
}
