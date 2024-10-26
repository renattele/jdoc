package jdoc.presentation;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jdoc.data.SocketClient;
import jdoc.data.SocketHost;
import jdoc.domain.*;
import jdoc.presentation.components.MarkdownTextArea;
import jdoc.presentation.components.UserAvatar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DocumentController {
    @FXML
    private VBox container;

    @FXML
    private MarkdownTextArea area;

    @FXML
    private HBox clientsContainer;

    @FXML
    private TextField clientName;

    private String incomingText;
    private final Gson gson = Serializer.gson();
    private Client client;

    @FXML
    public void initialize() {
        VBox.setVgrow(container, Priority.ALWAYS);
        var host = new SocketHost(8080);
        new HostManager(host, gson);
        try {
            client = new SocketClient("localhost", 8080);
            ClientManager clientManager = new ClientManager(client, gson);
            clientManager.allClients().subscribe(this::manageAvatars);
            clientName.textProperty().addListener((observable, oldValue, newValue) -> {
                manageUsername(newValue);
            });
            area.textProperty().addListener((observable, oldValue, newValue) -> {
                manageTextChanges(oldValue, newValue);
            });
            client.incoming().subscribe(this::manageIncomingChanges);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageIncomingChanges(Message message) {
        if (message.type() == Message.EDIT) {
            var json = message.dataString();
            var listType = new TypeToken<ArrayList<AbstractDelta<String>>>() {
            }.getType();
            List<AbstractDelta<String>> diffs = gson.fromJson(json, listType);
            try {
                var patch = new Patch<String>();
                for (AbstractDelta<String> diff : diffs) {
                    patch.addDelta(diff);
                }
                List<String> newLines = patch.applyTo(Arrays.asList(area.getText().split("\n")));
                var newText = String.join("\n", newLines);
                incomingText = newText;
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
            } catch (PatchFailedException e) {
                e.printStackTrace();
            }
        }
    }

    private void manageTextChanges(String oldValue, String newValue) {
        if (Objects.equals(newValue, incomingText)) {
            incomingText = null;
            return;
        }
        var diff = DiffUtils.diff(Arrays.asList(oldValue.split("\n")), Arrays.asList(newValue.split("\n")));
        var diffJson = gson.toJson(diff.getDeltas());
        client.send(new Message(Message.EDIT, diffJson));
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