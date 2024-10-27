package jdoc.presentation.components;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class RecentLocationButton extends HBox {
    private final ImageView closeButtonImage;
    private final Label label;
    public RecentLocationButton(String text) {
        closeButtonImage = new ImageView("close.png");
        label = new Label(text);
        closeButtonImage.setFitWidth(24);
        closeButtonImage.setFitHeight(24);
        getStyleClass().add("recent-location-button");
        setAlignment(Pos.CENTER);
        getChildren().add(label);
        setSpacing(4);
        setCursor(Cursor.HAND);
        getChildren().add(closeButtonImage);
    }
    public void setOnDelete(EventHandler<? super MouseEvent> handler) {
        closeButtonImage.setOnMouseClicked(handler);
    }
    public void setOnClick(EventHandler<? super MouseEvent> handler) {
        label.setOnMouseClicked(handler);
    }
}
