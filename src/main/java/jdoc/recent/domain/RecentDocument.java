package jdoc.recent.domain;

import lombok.Builder;

@Builder(toBuilder = true)
public record RecentDocument(Type type, String remoteUrl, String localUrl, String displayName) {
    public enum Type {
        Local,
        Remote
    }
}
