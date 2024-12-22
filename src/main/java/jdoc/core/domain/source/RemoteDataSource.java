package jdoc.core.domain.source;

import jdoc.core.domain.change.Change;

import java.io.IOException;

public interface RemoteDataSource<CHANGE extends Change<?, CHANGE>> extends DataSource<CHANGE> {
    interface Factory<CHANGE extends Change<?, CHANGE>> {
        DataSource<CHANGE> create(String url) throws IOException;
    }
}
