package jdoc.document.domain.change.user;

import jdoc.document.domain.User;

import java.util.List;

public record AddUserChange(User user) implements UserChange {
    @Override
    public void apply(List<User> to) {
        to.add(user);
    }
}
