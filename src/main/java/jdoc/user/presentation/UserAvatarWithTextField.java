package jdoc.user.presentation;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class UserAvatarWithTextField extends HBox {
    private final TextField textField = new TextField();
    private final UserAvatar userAvatar;

    @SuppressWarnings("CssUnresolvedCustomProperty")
    public UserAvatarWithTextField(String name) {
        var children = getChildren();
        userAvatar = new UserAvatar(name);
        children.add(userAvatar);
        children.add(textField);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            userAvatar.setName(newValue);
        });
        setAlignment(Pos.CENTER);
        getStyleClass().add("user-avatar-with-text-field");
    }

    public void setName(String name) {
        userAvatar.setName(name);
        textField.setText(name);
    }

    public ReadOnlyStringProperty nameProperty() {
        return textField.textProperty();
    }
}
