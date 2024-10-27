package jdoc.presentation;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.jgit.HistogramDiff;
import com.github.difflib.patch.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.processors.PublishProcessor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jdoc.data.Serializer;
import jdoc.data.SocketClient;
import jdoc.data.SocketHost;
import jdoc.domain.*;
import jdoc.presentation.components.MarkdownTextArea;
import jdoc.presentation.components.UserAvatar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"ResultOfMethodCallIgnored", "CallToPrintStackTrace"})
public class DocumentController extends Controller<String> {
    @FXML
    private VBox container;

    @FXML
    private MarkdownTextArea area;

    @FXML
    private HBox clientsContainer;

    @FXML
    private TextField clientName;

    @FXML
    private ProgressIndicator progressIndicator;

    private final String sessionId = UUID.randomUUID().toString().substring(0, 6);
    private String incomingText;
    private final Gson gson = Serializer.gson();
    private Client client;
    private final PublishProcessor<String> text = PublishProcessor.create();

    @Override
    public void init() {
        VBox.setVgrow(container, Priority.ALWAYS);
        var host = new SocketHost(8080);
        new HostManager(host, gson);
        try {
            var uri = URI.create(argument);
            client = new SocketClient(uri.getHost(), uri.getPort());
            var clientManager = new ClientManager(client, gson);
            clientManager.allClients().subscribe(this::manageAvatars);
            clientName.textProperty().addListener((observable, oldValue, newValue) -> manageUsername(newValue));
            area.textProperty().addListener((observable, oldValue, newValue) -> manageTextChanges(oldValue, newValue));
            client.incoming().subscribe(this::manageIncomingChanges);
            text.debounce(2000, TimeUnit.MILLISECONDS).subscribe(this::save);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save(String text) {
        progressIndicator.setVisible(true);
        var firstLine = text.lines().findFirst();
        var fileName = firstLine.map(s -> s.replace("# ", "")).orElse("document").replace("/", "");
        var home = System.getenv("HOME");
        var jdocDir = new File(home + File.separator + "Documents", "jdoc");
        if (!jdocDir.exists()) jdocDir.mkdirs();
        var destination = new File(jdocDir, fileName + " (" + sessionId + ")" + ".md");
        if (!destination.exists()) {
            try {
                destination.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (var writer = new PrintWriter(destination)) {
            writer.println(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        progressIndicator.setVisible(false);
    }

    private void manageIncomingChanges(Message message) {
        if (message.type() == Message.EDIT) {
            System.out.println("RECEIVE");
            var json = message.dataString();
            var listType = new TypeToken<ArrayList<AbstractDelta<String>>>() {
            }.getType();
            List<AbstractDelta<String>> diffs = gson.fromJson(json, listType);
            try {
                var patch = new Patch<String>();
                for (AbstractDelta<String> diff : diffs) {
                    patch.addDelta(diff);
                }
                var originalLines = Arrays.asList(area.getText().split("\n"));
                List<String> newLines = patch.applyTo(originalLines);
                var newText = String.join("\n", newLines);
                incomingText = newText;
                updateText(newText);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateText(String newText) {
        Platform.runLater(() -> {
            var selection = area.getSelection();
            var position = area.getCaretPosition();
            area.replaceText(newText);
            var length = area.getLength();
            area.moveTo(Math.min(position, length));
            area.selectRange(
                    Math.min(selection.getStart(), length),
                    Math.min(selection.getEnd(), length)
            );
        });
    }

    private void manageTextChanges(String oldValue, String newValue) {
        text.onNext(newValue);
        if (Objects.equals(newValue, incomingText)) {
            incomingText = null;
            return;
        }
        var oldValueWithEmptyLines = Arrays.asList(oldValue.split("\n"));
        var newValueWithEmptyLines = Arrays.asList(newValue.split("\n"));
        var diff = DiffUtils.diff(oldValueWithEmptyLines, newValueWithEmptyLines, new HistogramDiff<>());
        if (!diff.getDeltas().isEmpty()) {
            var diffJson = gson.toJson(diff.getDeltas());
            client.send(new Message(Message.EDIT, diffJson));
        }
    }

    private void manageUsername(String newValue) {
        client.send(new Message(Message.SET_USERNAME, newValue));
    }

    private void manageAvatars(List<ClientEntity> clients) {
        Platform.runLater(() -> {
            var children = clientsContainer.getChildren();
            children.removeIf(node -> node instanceof UserAvatar);
            for (ClientEntity clientEntity : clients) {
                children.add(0, new UserAvatar(clientEntity.name()));
            }
        });
    }
}