module jdoc {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.fxmisc.richtext;
    requires java.desktop;
    requires com.google.gson;
    requires io.reactivex.rxjava3;
    requires io.github.javadiffutils;

    exports jdoc.domain;
    opens jdoc.domain to javafx.fxml;
    exports jdoc.data;
    opens jdoc.data to javafx.fxml;
    exports jdoc.presentation;
    opens jdoc.presentation to javafx.fxml;
    exports jdoc.presentation.components;
    opens jdoc.presentation.components to javafx.fxml;
}