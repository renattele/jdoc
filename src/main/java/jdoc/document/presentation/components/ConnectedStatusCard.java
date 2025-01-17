package jdoc.document.presentation.components;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class ConnectedStatusCard extends HBox {
    public ConnectedStatusCard(Supplier<Boolean> isConnected) {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    setIsConnected(isConnected.get());
                });
            }
        }, 0, 1000);
    }

    public void setIsConnected(boolean isConnected) {
        var imageSource = isConnected ? "/icons/ic_connected.png" : "/icons/ic_disconnected.png";
        var image = new ImageView(new Image(imageSource));
        image.setFitWidth(36);
        image.setFitHeight(36);
        var name = isConnected ? "Connected" : "Disconnected";
        setSpacing(8);
        setAlignment(Pos.CENTER);
        getChildren().clear();
        getChildren().add(image);
        getChildren().add(new Label(name));
    }
}
