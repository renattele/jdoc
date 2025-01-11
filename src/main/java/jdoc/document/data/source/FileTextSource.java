package jdoc.document.data.source;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.processors.ReplayProcessor;
import jdoc.core.util.DisposableList;
import jdoc.document.data.StringTextBuilder;
import jdoc.document.domain.change.InsertTextChange;
import jdoc.document.domain.change.TextChange;
import jdoc.document.domain.source.LocalTextSource;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FileTextSource implements LocalTextSource {
    private final DisposableList disposables = new DisposableList();
    private final ReplayProcessor<TextChange> changes = ReplayProcessor.create();
    private final StringBuilder text = new StringBuilder();

    public FileTextSource(File file, boolean emitChangesFromFile) {
        try {
            if (!file.exists()) {
                Files.createFile(file.toPath());
            }
            disposables.add(changes.debounce(2000, TimeUnit.MILLISECONDS).subscribe(i -> {
                try (var writer = new BufferedWriter(new FileWriter(file))) {
                    writer.append(text.toString());
                    writer.flush();
                }
            }));
            if (emitChangesFromFile) {
                var text = Files.readString(file.toPath());
                log.info(text);
                changes.onNext(new InsertTextChange(0, text));
            }
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
    }

    public FileTextSource(File file) {
        this(file, true);
    }

    @Override
    public void apply(TextChange textChange) {
        textChange.apply(new StringTextBuilder(text));
        changes.onNext(textChange);
    }

    @Override
    public Flowable<TextChange> changes() {
        return changes;
    }

    @Override
    public void close() throws Exception {
        disposables.close();
    }

    public static class Factory implements LocalTextSource.Factory {

        @Override
        public LocalTextSource create(File file, boolean emitChangesFromFile) {
            return new FileTextSource(file, emitChangesFromFile);
        }
    }
}
