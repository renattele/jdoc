package jdoc.document.domain.change;

import jdoc.document.domain.TextBuilder;

public class ClearTextChange implements TextChange {
    @Override
    public void apply(TextBuilder text) {
        text.delete(0, text.length());
    }
}
