package jdoc.document.data;

import jdoc.document.domain.TextBuilder;

public class StringTextBuilder implements TextBuilder {
    private final StringBuilder builder;
    public StringTextBuilder() {
        this.builder = new StringBuilder();
    }
    public StringTextBuilder(String text) {
        this.builder = new StringBuilder(text);
    }
    public StringTextBuilder(StringBuilder builder) {
        this.builder = builder;
    }
    @Override
    public TextBuilder insert(int index, String value) {
        builder.insert(index, value);
        return this;
    }

    @Override
    public TextBuilder delete(int start, int end) {
        builder.delete(start, end);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
