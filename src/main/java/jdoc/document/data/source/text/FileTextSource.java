package jdoc.document.data.source.text;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import jdoc.document.data.StringTextBuilder;
import jdoc.document.domain.change.text.TextChange;
import jdoc.document.domain.change.text.ClearTextChange;
import jdoc.document.domain.change.text.InsertTextChange;
import jdoc.document.domain.source.text.LocalTextSource;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileTextSource implements LocalTextSource {
    private final List<Disposable> disposables = new ArrayList<>();
    private final PublishProcessor<TextChange> changes = PublishProcessor.create();
    private final StringBuilder text = new StringBuilder();

    public FileTextSource(File file) {
        disposables.add(changes.debounce(2000, TimeUnit.MILLISECONDS).subscribe(i -> {
            try (var writer = new BufferedWriter(new FileWriter(file))) {
                writer.append(text.toString());
                writer.flush();
            }
        }));
        disposables.add(Flowable.interval(0, 5000, TimeUnit.MILLISECONDS).subscribe(l -> {
            var textFromFile = Files.readString(file.toPath());
            changes.onNext(new ClearTextChange());
            changes.onNext(new InsertTextChange(0, textFromFile));
        }));
    }

    @Override
    public void apply(TextChange textChange) {
        changes.onNext(textChange);
        textChange.apply(new StringTextBuilder(text));
    }

    @Override
    public Flowable<TextChange> changes() {
        return changes;
    }

    @Override
    public void close() {
        disposables.forEach(Disposable::dispose);
    }

    public static class Factory implements LocalTextSource.Factory {

        @Override
        public LocalTextSource create(File file) {
            return new FileTextSource(file);
        }
    }
}
