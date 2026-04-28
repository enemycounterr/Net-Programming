package org.networks.subject7.individual8.server;

import org.networks.subject7.individual8.model.ChangeType;
import org.networks.subject7.individual8.model.FileChangeEvent;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

public class DirectoryMonitoring implements Runnable {
    private final Path directory;
    private final BlockingQueue<FileChangeEvent> eventQueue;

    public DirectoryMonitoring(Path directory, BlockingQueue<FileChangeEvent> eventQueue) {
        this.directory = directory;
        this.eventQueue = eventQueue;
    }


    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            directory.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            System.out.println("Monitoring directory: " + directory);

            while (!Thread.currentThread().isInterrupted()) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path fileName = (Path) event.context();

                    ChangeType changeType = mapEventKind(kind);
                    if (changeType != null) {
                        FileChangeEvent fileEvent = new FileChangeEvent(
                                fileName.toString(),
                                changeType,
                                directory.resolve(fileName).toString(),
                                LocalDateTime.now()
                        );
                        eventQueue.put(fileEvent);
                    }
                }

                if (!key.reset()) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Directory monitoring stopped: " + e.getMessage());
        }

    }

    private ChangeType mapEventKind(WatchEvent.Kind<?> kind) {
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) return ChangeType.CREATED;
        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) return ChangeType.MODIFIED;
        if (kind == StandardWatchEventKinds.ENTRY_DELETE) return ChangeType.DELETED;
        return null;
    }
}
