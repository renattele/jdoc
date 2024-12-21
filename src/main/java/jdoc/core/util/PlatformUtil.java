package jdoc.core.util;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class PlatformUtil {
    public static void runLaterAndWait(Runnable runnable) {
        var latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            runnable.run();
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
