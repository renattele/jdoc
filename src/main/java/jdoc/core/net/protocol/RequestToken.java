package jdoc.core.net.protocol;

import java.util.concurrent.ThreadLocalRandom;

public record RequestToken(long value) {
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();
    public static RequestToken generate() {
        long value = random.nextLong(0L, Long.MAX_VALUE);
        return new RequestToken(value);
    }

    public static final RequestToken EMPTY = new RequestToken(-1L);
}
