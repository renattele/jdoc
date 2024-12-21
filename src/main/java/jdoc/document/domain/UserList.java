package jdoc.document.domain;

import java.util.List;

public interface UserList {
    List<User> users();
    void add(User user);
    void remove(User user);
}
