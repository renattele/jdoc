package jdoc.document.domain.change.user;

import jdoc.document.domain.User;
import jdoc.document.domain.change.Change;

import java.util.List;

public record AddMultipleUsersChange(List<User> users) implements UserChange {
    @Override
    public void apply(List<User> to) {
        to.addAll(users);
    }
}
