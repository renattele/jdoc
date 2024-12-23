package jdoc;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdoc.core.data.JacksonSerializer;
import jdoc.core.net.client.impl.MapClientConnectionDataSource;
import jdoc.core.net.protocol.MessageType;
import jdoc.core.presentation.Controller;
import jdoc.core.data.LocalSettings;
import jdoc.core.data.ReadOnlyModule;
import jdoc.core.data.source.BlockingDataSourceOrchestrator;
import jdoc.core.data.source.ClientConnectionGenericDataSource;
import jdoc.document.data.DocumentRepositoryImpl;
import jdoc.document.data.source.FileTextSource;
import jdoc.user.data.UserRepositoryImpl;
import jdoc.document.domain.change.TextChange;
import jdoc.user.domain.change.UserChange;
import jdoc.recent.data.RecentDocumentsRepositoryImpl;
import jdoc.core.di.Module;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class App extends Application {
	public static Stage stage;
	private static App app;
	private static Module module;

	@FXML
	public void exitApplication(ActionEvent event) {
		Platform.exit();
	}

	@Override
	public void start(Stage stage) {
		log.debug("HELLO");
		initModule();
		App.stage = stage;
		stage.setOnCloseRequest(event -> stage.close());
		app = this;
		navigate("/chooser-view.fxml");
		stage.show();
	}

	private void initModule() {
		var serializer = new JacksonSerializer();
		var settings = new LocalSettings(serializer);
		var recentDocumentsRepository = new RecentDocumentsRepositoryImpl(settings);
		var clientConnectionDataSource = new MapClientConnectionDataSource();
		var orchestratorFactory = new BlockingDataSourceOrchestrator.Factory();
		var localTextSourceFactory = new FileTextSource.Factory();
		var remoteTextSourceFactory = new ClientConnectionGenericDataSource.Factory<>(serializer,
				clientConnectionDataSource,
                MessageType.EDIT_DOCUMENT,
                MessageType.DOCUMENT_SYNC_REQUEST,
                MessageType.DOCUMENT_SYNC_RESPONSE,
                TextChange.class);
		var remoteUserSourceFactory = new ClientConnectionGenericDataSource.Factory<>(serializer,
				clientConnectionDataSource,
				MessageType.EDIT_USERS,
				MessageType.USERS_SYNC_REQUEST,
				MessageType.USERS_SYNC_RESPONSE,
				UserChange.class);
		var userRepository = new UserRepositoryImpl(orchestratorFactory, remoteUserSourceFactory);
		var documentRepository = new DocumentRepositoryImpl(orchestratorFactory, localTextSourceFactory, remoteTextSourceFactory);
		module = new ReadOnlyModule(
				serializer,
				settings,
				localTextSourceFactory,
				recentDocumentsRepository,
				documentRepository,
				userRepository
		);
	}

	public static void browse(String url) {
		app.getHostServices().showDocument(url);
	}

	public static Optional<File> saveFile(String description, String... supportedFiles) {
		var chooser = new FileChooser();
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, supportedFiles));
		return Optional.ofNullable(new FileChooser().showSaveDialog(stage));
	}

	public static Optional<File> openFile(String description, String... supportedFiles) {
		var chooser = new FileChooser();
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, supportedFiles));
		return Optional.ofNullable(chooser.showOpenDialog(stage));
	}

	public static void navigate(String file, Object argument) {
        try {
			var loader = new FXMLLoader(App.class.getResource(file));
            var scene = (Parent) loader.load();
			Controller<Object> controller = loader.getController();
			controller.setModule(module);
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
