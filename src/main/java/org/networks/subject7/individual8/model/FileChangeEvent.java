package org.networks.subject7.individual8.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public record FileChangeEvent(String fileName,
                              ChangeType changeType,
                              String path,
                              LocalDateTime time
) implements Serializable {

    @Override
    public String toString() {
        return String.format("[%s] %s: %s at %s", time, changeType, fileName, path);
    }
}
