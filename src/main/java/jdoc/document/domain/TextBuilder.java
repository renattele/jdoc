package jdoc.document.domain;

@SuppressWarnings("UnusedReturnValue")
public interface TextBuilder {
    TextBuilder insert(int index, String value);
    TextBuilder delete(int start, int end);
    @Override
    String toString();

    default int length() {
        return toString().length();
    }
}
