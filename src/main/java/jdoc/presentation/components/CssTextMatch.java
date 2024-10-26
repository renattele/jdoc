package jdoc.presentation.components;

import java.util.regex.Pattern;

public record CssTextMatch(String regex, String cssClass) {
    public boolean matches(String str) {
        return Pattern.compile(regex).matcher(str).matches();
    }
}