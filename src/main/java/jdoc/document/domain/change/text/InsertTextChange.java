package jdoc.document.domain.change.text;

import jdoc.document.domain.TextBuilder;

public record InsertTextChange(int position, String content) implements TextChange {
    @Override
    public void apply(TextBuilder text) {
        text.insert(Math.min(position, text.length()), content);
    }
}
