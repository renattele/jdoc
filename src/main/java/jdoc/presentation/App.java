package jdoc.presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
	public static Stage stage;
	@Override
	public void start(Stage stage) {
		App.stage = stage;
		navigate("/chooser-view.fxml");
		stage.show();
	}

	public static void navigate(String file, Object argument) {
        try {
			var loader = new FXMLLoader(App.class.getResource(file));
            var scene = (Parent) loader.load();
			Controller<Object> controller = loader.getController();
			controller.setArgument(argument);
			controller.init();
			scene.getStylesheets().add("base.css");
			stage.setScene(new Scene(scene));
        } catch (IOException e) {
			e.printStackTrace();
        }
	}
	public static void navigate(String file) {
		navigate(file, null);
	}

	public static void main(String[] args) {
		launch();
	}
}
