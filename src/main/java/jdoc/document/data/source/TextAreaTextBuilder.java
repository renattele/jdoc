package jdoc.document.data.source;

import jdoc.document.domain.TextBuilder;
import lombok.AllArgsConstructor;
import org.fxmisc.richtext.StyledTextArea;

@AllArgsConstructor
public class TextAreaTextBuilder implements TextBuilder {
    private final StyledTextArea<?, ?> area;

    @Override
    public TextBuilder insert(int index, String value) {
        area.insertText(index, value);
        return this;
    }

    @Override
    public TextBuilder delete(int start, int end) {
        area.deleteText(start, end);
        return this;
    }

    @Override
    public String toString() {
        return area.getText();
    }
}
