package jdoc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jdoc.core.data.JacksonSerializer;
import jdoc.core.net.client.impl.MapClientConnectionDataSource;
import jdoc.core.net.protocol.Message;
import jdoc.core.presentation.Controller;
import jdoc.core.data.LocalSettings;
import jdoc.core.data.ReadOnlyModule;
import jdoc.document.data.BlockingDataSourceOrchestrator;
import jdoc.document.data.source.ClientConnectionGenericDataSource;
import jdoc.document.data.DocumentRepositoryImpl;
import jdoc.document.data.source.text.FileTextSource;
import jdoc.document.data.user.UserRepositoryImpl;
import jdoc.document.domain.UserRepository;
import jdoc.document.domain.change.text.TextChange;
import jdoc.document.domain.change.user.UserChange;
import jdoc.recent.data.RecentDocumentsRepositoryImpl;
import jdoc.core.di.Module;

import java.io.IOException;

public class App extends Application {
	public static Stage stage;
	private static App app;
	private static Module module;

	@Override
	public void start(Stage stage) {
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
                Message.EDIT_DOCUMENT,
                Message.DOCUMENT_SYNC_REQUEST,
                Message.DOCUMENT_SYNC_RESPONSE,
                TextChange.class);
		var remoteUserSourceFactory = new ClientConnectionGenericDataSource.Factory<>(serializer,
				clientConnectionDataSource,
				Message.EDIT_USERS,
				Message.USERS_SYNC_REQUEST,
				Message.USERS_SYNC_RESPONSE,
				UserChange.class);
		var userRepositoryFactory = new UserRepositoryImpl.Factory(orchestratorFactory, remoteUserSourceFactory);
		var documentRepository = new DocumentRepositoryImpl(orchestratorFactory, localTextSourceFactory, remoteTextSourceFactory);
		module = new ReadOnlyModule(
				serializer,
				settings,
				recentDocumentsRepository,
				documentRepository,
				userRepositoryFactory
		);
	}

	public static void browse(String url) {
		app.getHostServices().showDocument(url);
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
