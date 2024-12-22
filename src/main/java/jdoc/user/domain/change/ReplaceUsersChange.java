package jdoc.user.domain.change;

import jdoc.user.domain.User;

import java.util.Map;

public record ReplaceUsersChange(Map<String, User> users) implements UserChange {
    @Override
    public void apply(Map<String, User> to) {
        to.putAll(users);
    }
}
