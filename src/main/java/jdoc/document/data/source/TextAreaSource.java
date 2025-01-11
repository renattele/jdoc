package jdoc.document.data.source;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import javafx.application.Platform;
import jdoc.document.domain.change.TextChange;
import jdoc.document.domain.change.DeleteTextChange;
import jdoc.document.domain.change.InsertTextChange;
import jdoc.document.domain.source.TextSource;
import jdoc.core.util.PlatformUtil;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.richtext.StyledTextArea;

@Slf4j
@ExtensionMethod({PlatformUtil.class})
public class TextAreaSource implements TextSource {
    private final PublishProcessor<TextChange> changes = PublishProcessor.create();
    private final StyledTextArea<?, ?> textArea;

    public TextAreaSource(StyledTextArea<?, ?> textArea) {
        this.textArea = textArea;
        textArea.plainTextChanges().subscribe(change -> {
            TextChange textChange;
            if (change.getRemoved().isEmpty()) {
                textChange = new InsertTextChange(change.getPosition(), change.getInserted());
            } else {
                textChange = new DeleteTextChange(change.getPosition(), change.getPosition() + change.getRemoved().length());
            }
            changes.onNext(textChange);
        });
    }

    @Override
    public void apply(TextChange textChange) {
        mutate(() -> {
            var builder = new TextAreaTextBuilder(textArea);
            log.info("BEFORE: {}", builder);
            textChange.apply(builder);
            log.info("AFTER: {}", builder);
        });
    }

    private void mutate(Runnable runnable) {
        Platform.runLater(runnable);
    }

    @Override
    public Flowable<TextChange> changes() {
        return changes;
    }

    @Override
    public void close() {}
}
