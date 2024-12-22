package jdoc.document.domain.change;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jdoc.document.data.StringTextBuilder;
import jdoc.document.domain.TextBuilder;
import jdoc.core.domain.change.Change;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClearTextChange.class, name = "text_clear"),
        @JsonSubTypes.Type(value = DeleteTextChange.class, name = "text_delete"),
        @JsonSubTypes.Type(value = InsertTextChange.class, name = "text_insert")
})
public interface TextChange extends Change<TextBuilder, TextChange> {
    @Override
    default TextChange reduce(TextChange change) {
        var textBuilder = new StringTextBuilder();
        change.apply(textBuilder);
        return new InsertTextChange(0, textBuilder.toString());
    }
}
