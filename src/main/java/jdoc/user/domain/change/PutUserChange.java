package jdoc.user.domain.change;

import jdoc.user.domain.User;

import java.util.Map;

public record PutUserChange(User user) implements UserChange {
    @Override
    public void apply(Map<String, User> to) {
        to.put(user.id(), user);
    }
}
