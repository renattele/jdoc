package jdoc.user.presentation;

import com.github.javafaker.Faker;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.processors.PublishProcessor;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import jdoc.core.domain.source.DataSource;
import jdoc.user.domain.User;
import jdoc.user.domain.UserList;
import jdoc.user.domain.change.PutUserChange;
import jdoc.user.domain.change.UserChange;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class UserRow extends HBox implements DataSource<UserChange> {
    private final Map<String, User> users = new HashMap<>();
    private final FlowableProcessor<UserChange> changes = PublishProcessor.create();
    private User currentUser = new User(
            new Faker().name().firstName(),
            UUID.randomUUID().toString()
    );

    public UserRow(UserList userList) {
        userList.addSource(this);
        Flowable.interval(10, TimeUnit.SECONDS).subscribe(i -> {
            currentUser = new User(currentUser.name(), currentUser.id());
            apply(new PutUserChange(currentUser));
        });
        var fieldWithAvatar = new UserAvatarWithTextField(currentUser.name());
        apply(new PutUserChange(currentUser));
        fieldWithAvatar.setName(currentUser.name());
        fieldWithAvatar.nameProperty().addListener((observable, oldValue, newValue) -> {
            currentUser = new User(newValue, currentUser.id());
            apply(new PutUserChange(currentUser));
        });
        setSpacing(8);
        getChildren().add(fieldWithAvatar);
    }

    @Override
    public void apply(UserChange change) {
        change.apply(users);
        var userListCopy = Map.copyOf(users);
        Platform.runLater(() -> {
            var children = getChildren();
            children.removeIf(child -> !(child instanceof UserAvatarWithTextField));
            System.out.println("USERS: " + userListCopy + ". CHANGE: " + change);
            for (User user : userListCopy.values()) {
                if (user.equals(currentUser)) continue;
                children.add(0, new UserAvatar(user.name()));
            }
        });
        changes.onNext(change);
    }

    @Override
    public Flowable<UserChange> changes() {
        return changes;
    }

    @Override
    public void close() {
    }
}
