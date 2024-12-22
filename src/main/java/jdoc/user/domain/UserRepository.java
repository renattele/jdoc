package jdoc.user.domain;

public interface UserRepository {
    UserList getUsersByUrl(String url);
}