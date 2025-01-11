package jdoc.core.presentation;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileChooserOptions {
    private final String title;
    private final String extension;
    private final String description;
}