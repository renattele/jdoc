package jdoc.user.domain;

public record User(String name, String id, Long lastSeen) {
    public User(String name, String id) {
        this(name, id, System.currentTimeMillis());
    }
}
