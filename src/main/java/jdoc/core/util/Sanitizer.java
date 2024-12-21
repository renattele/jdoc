package jdoc.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Sanitizer {
    public String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._]+", "_");
    }
}