package jdoc.document.presentation.components;

import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Random;

public class UserAvatar extends StackPane {

    public UserAvatar(String name) {
        var random = new Random(name.hashCode());
        var animal = Math.abs(name.hashCode()) % 71;
        var url = "/animals/" + animal + ".png";
        var imageResource = getClass().getResource(url);
        if (imageResource == null) throw new IllegalStateException("Image not found: " + url);
        var imageView = new ImageView(imageResource.toString());
        imageView.setFitWidth(36);
        imageView.setFitHeight(36);
        imageView.setPreserveRatio(false);
        var hue = random.nextFloat() * 360f;
        var color = Color.hsb(hue, 0.5, 0.2);
        var tooltip = new Tooltip(name);
        Tooltip.install(imageView, tooltip);
        setStyle("-fx-background-color: linear-gradient(from 50% 0% to 50% 100%, " + hex(color.brighter()) + ", " + hex(color) + "); -fx-background-radius: 50%; -fx-padding: 12");
        getChildren().add(imageView);
    }

    private String hex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
