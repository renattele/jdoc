package jdoc.user.presentation;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.util.Random;

public class UserAvatar extends Canvas {

    public UserAvatar(String name) {
        super(48, 48);
        setName(name);
    }

    public void setName(String name) {
        var gc = getGraphicsContext2D();
        var random = new Random(name.hashCode());
        var animal = Math.abs(name.hashCode()) % 71;
        var url = "/animals/" + animal + ".png";
        var imageResource = getClass().getResource(url);
        if (imageResource == null) throw new IllegalStateException("Image not found: " + url);
        var image = new Image(imageResource.toString());
        var hue = random.nextFloat() * 360f;
        var color = Color.hsb(hue, 0.5, 0.2);
        var tooltip = new Tooltip(name);
        Tooltip.install(this, tooltip);
        gc.setFill(
                new LinearGradient(0,
                        0,
                        1,
                        1,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0, color.brighter()),
                        new Stop(1, color)
                ));
        gc.fillRoundRect(0, 0, getWidth(), getHeight(), getWidth(), getHeight());
        gc.drawImage(image, 9, 9, 33, 33);
    }
}
