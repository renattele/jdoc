package jdoc.document.presentation.components;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import jdoc.App;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class MarkdownTextArea extends StyleClassedTextArea {
    public static CssTextMatch ITALIC = new CssTextMatch("(?<!\\*)\\*(?!\\*|\\*\\*)(.+?)\\*(?!\\*)\n", "italic");
    public static CssTextMatch BOLD = new CssTextMatch("(?<!\\*)\\*\\*(?!\\*)(.+?)\\*\\*(?!\\*)", "bold");
    public static CssTextMatch BOLD_ITALIC = new CssTextMatch("(?<!\\*)\\*\\*\\*(.+?)\\*\\*\\*(?!\\*)\n", "bold_italic");
    public static CssTextMatch HEADER1 = new CssTextMatch("#(.*?)\\n", "header1");
    public static CssTextMatch HEADER2 = new CssTextMatch("##(.*?)\\n", "header2");
    public static CssTextMatch HEADER3 = new CssTextMatch("###(.*?)\\n", "header3");
    public static CssTextMatch LINK = new CssTextMatch("\\[.*?\\]\\((.*?)\\)", "link");
    public static CssTextMatch UNORDERED_LIST = new CssTextMatch("^([*\\-+])\\s+(.*)$", "");
    public static CssTextMatch ORDERED_LIST = new CssTextMatch("^(\\d+)\\.\\s+(.*)$", "");
    public static CssTextMatch MONOSPACE = new CssTextMatch("`.*?`", "monospace");
    public static CssTextMatch CODE = new CssTextMatch("(?s)```.*?```", "code");
    public static CssTextMatch STRIKETHROUGH = new CssTextMatch("~~(.*?)~~", "strikethrough");
    public static CssTextMatch BLOCKQUOTE = new CssTextMatch("^>(.*?)\\n", "blockquote");
    public static List<CssTextMatch> matches = List.of(
            HEADER1,
            HEADER2,
            HEADER3,
            LINK,
            UNORDERED_LIST,
            MONOSPACE,
            CODE,
            ITALIC,
            BOLD,
            BOLD_ITALIC,
            STRIKETHROUGH,
            BLOCKQUOTE
    );

    public MarkdownTextArea() {
        setStyle("-fx-font-size: 20");
        textProperty().addListener((obs, oldText, newText) -> {
            render();
        });
        lengthProperty().addListener((obs, oldLength, newLength) -> {
            render();
        });
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                var caret = getCaretPosition();
                var lines = getText().substring(0, caret).split("\n");
                if (lines.length == 0) return;
                var currentLine = lines[lines.length - 1];
                if (ORDERED_LIST.matches(currentLine)) {
                    int lastNumber = Integer.parseInt(currentLine.split("\\.")[0]);
                    int nextNumber = lastNumber + 1;
                    String nextLine = nextNumber + ". ";
                    insertText(caret, nextLine);
                    moveTo(caret + nextLine.length());
                } else if (UNORDERED_LIST.matches(currentLine)) {
                    var lastSymbol = String.valueOf(currentLine.charAt(0));
                    var nextLine = lastSymbol + " ";
                    insertText(caret, nextLine);
                    moveTo(caret + nextLine.length());
                }
            }
        });
        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int index = hit(event.getX(), event.getY()).getCharacterIndex().orElse(-1);
                var link = getLink(index);
                if (link != null) {
                    try {
                        App.browse(link);
                        deselect();
                    } catch (Exception e) {
                        log.error(e.toString(), e);
                    }
                }
            }
        });
        setParagraphGraphicFactory(value -> {
            var region = new Region();
            var paragraph = getParagraph(value).getText();
            var padding = 0;
            if (UNORDERED_LIST.matches(paragraph) || ORDERED_LIST.matches(paragraph) || BLOCKQUOTE.matches(paragraph + "\n")) {
                padding = 20;
            }
            region.setMinWidth(padding);
            return region;
        });
    }

    private void render() {
        clearStyle(0, getText().length());
        for (CssTextMatch match : matches) {
            renderWithPattern(match.regex(), match.cssClass());
        }
    }

    private String getLink(int caretIndex) {
        if (caretIndex < 0) return null;
        var pattern = Pattern.compile(LINK.regex());
        String text = getText();
        var matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (start <= caretIndex && caretIndex <= end) {
                int linkStart = matcher.start(1);
                int linkEnd = matcher.end(1);
                return text.substring(linkStart, linkEnd);
            }
        }
        return null;
    }

    private void renderWithPattern(String patternStr, String css) {
        var pattern = Pattern.compile(patternStr);
        String text = getText();
        var matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            log.info("START: {} END: {} CSS: {}", start, end, css);
            style(start, end, css);
        }
    }

    private void style(int start, int end, String css) {
        var oldStyles = getStyleAtPosition(start);
        var newStyles = new ArrayList<>(oldStyles);
        newStyles.add(css);
        setStyle(start, end, newStyles);
    }
}
