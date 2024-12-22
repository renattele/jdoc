package jdoc.user.domain.change;

import jdoc.user.domain.User;

import java.util.List;
import java.util.Map;

public record RemoveUserChange(String userId) implements UserChange {
    @Override
    public void apply(Map<String, User> to) {
        to.remove(userId);
    }
}
