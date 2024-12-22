package jdoc.document.domain.change;

import jdoc.document.domain.TextBuilder;

public record DeleteTextChange(int start, int end) implements TextChange {
    @Override
    public void apply(TextBuilder text) {
        text.delete(Math.min(start, text.length()), Math.min(end, text.length()));
    }
}
