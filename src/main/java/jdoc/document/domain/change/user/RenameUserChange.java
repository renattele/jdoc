package jdoc.document.domain.change.user;

import jdoc.document.domain.User;

import java.util.List;

public record RenameUserChange(User user) implements UserChange {
    @Override
    public void apply(List<User> to) {
        for (int index = 0; index < to.size(); index++) {
            User currentUser = to.get(index);
            if (currentUser.id().equals(user.id())) {
                to.set(index, user);
                return;
            }
        }
        to.add(user);
    }
}
