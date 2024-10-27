package jdoc.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import jdoc.data.Settings;

public class ChooserController extends Controller<Object> {
    @FXML
    private VBox container;

    @FXML
    private TextField connectField;

    @FXML
    private FlowPane recentContainer;

    @Override
    public void init() {
        updateRecentUrls();
        connectField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Settings.addRecent(connectField.getText());
                updateRecentUrls();
            }
        });
    }

    private void updateRecentUrls() {
        var recent = Settings.getRecent();
        var children = recentContainer.getChildren();
        children.removeIf(child -> child instanceof Button);
        for (String url : recent) {
            var button = new Button(url);
            button.setOnMouseClicked(event -> App.navigate("/document-view.fxml", url));
            children.add(button);
        }
    }
}
