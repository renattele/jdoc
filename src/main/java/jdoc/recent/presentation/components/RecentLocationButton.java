package jdoc.recent.presentation.components;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class RecentLocationButton extends HBox {
    private final ImageView closeButtonImage;
    private final Label label;

    public RecentLocationButton(String text, String leaderImageUrl) {
        this(text, leaderImageUrl, true);
    }
    public RecentLocationButton(String text, String leaderImageUrl, boolean showDeleteButton) {
        var leaderImage = new ImageView(leaderImageUrl);
        label = new Label(text);
        closeButtonImage = new ImageView("icons/ic_close.png");
        closeButtonImage.setFitWidth(24);
        closeButtonImage.setFitHeight(24);
        leaderImage.setFitWidth(24);
        leaderImage.setFitHeight(24);
        getStyleClass().add("recent-location-button");
        setAlignment(Pos.CENTER);
        setSpacing(4);
        getChildren().add(leaderImage);
        getChildren().add(label);
        setCursor(Cursor.HAND);
        if (showDeleteButton) {
            getChildren().add(closeButtonImage);
        }
    }
    public void setOnDelete(EventHandler<? super MouseEvent> handler) {
        closeButtonImage.setOnMouseClicked(handler);
    }
    public void setOnClick(EventHandler<? super MouseEvent> handler) {
        label.setOnMouseClicked(handler);
    }
}
