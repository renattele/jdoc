package jdoc.presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
	private static Stage primaryStage;
	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		navigate("/document-view.fxml");
		stage.show();
	}

	public static void navigate(String file) {
        try {
            var scene = (Parent) FXMLLoader.load(App.class.getResource(file));
			scene.getStylesheets().add("base.css");
			primaryStage.setScene(new Scene(scene));
        } catch (IOException e) {
			e.printStackTrace();
        }
	}

	public static void main(String[] args) {
		launch();
	}
}
