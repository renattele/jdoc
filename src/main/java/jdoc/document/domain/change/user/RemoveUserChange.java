package jdoc.document.domain.change.user;

import jdoc.document.domain.User;

import java.util.List;

public record RemoveUserChange(String userId) implements UserChange {
    @Override
    public void apply(List<User> to) {
        to.removeIf(user -> user.id().equals(userId));
    }
}
