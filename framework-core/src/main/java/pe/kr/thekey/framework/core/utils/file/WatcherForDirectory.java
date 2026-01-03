package pe.kr.thekey.framework.core.utils.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class WatcherForDirectory {
    private WatchService watchService;
    private final Map<WatchKey, MonitorVo> directories = new HashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(false);

    public void init() throws IOException {
        if (!running.get()) {
            running.set(true);
            watchService = FileSystems.getDefault().newWatchService();
            new Thread(new Monitor()).start();
        }
    }

    public void stop() {
        running.set(false);
        try {
            watchService.close();
        } catch (IOException e) {
            log.error("Error closing watch service: {}", e.getMessage(), e);
        }
    }

    public void register(File directory, Callback<Path, FileEventType> callback) throws IOException {
        register(Paths.get(directory.toURI()), callback);
    }

    public void register(String directory, Callback<Path, FileEventType> callback) throws IOException {
        register(Paths.get(directory), callback);
    }

    public void register(Path directory, Callback<Path, FileEventType> callback) throws IOException {
        init();
        File pathFile = directory.toFile();
        if (pathFile.exists() && pathFile.isDirectory()) {
            WatchKey watchKey = directory.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.OVERFLOW);
            directories.put(watchKey, new MonitorVo(directory, callback));
        }
        else {
            log.warn("File does not exist or is not a regular file: {}", directory);
        }
    }

    private class Monitor implements Runnable {
        @Override
        public void run() {
            while (running.get()) {
                try {
                    WatchKey key = watchService.take();
                    boolean valid = key.isValid();
                    if(valid) {
                        key.pollEvents().forEach(watchEvent -> {
                            Object context = watchEvent.context();
                            FileEventType eventType = FileEventType.getEventType(watchEvent.kind());
                            try {
                                if (context instanceof Path path)
                                    directories.get(key).callback.accept(directories.get(key).getDirectory().resolve(path), eventType);
                                else
                                    directories.get(key).callback.accept(directories.get(key).getDirectory(), eventType);
                            } catch (Exception e) {
                                log.error("Error processing file watching event", e);
                            }
                        });
                        valid = key.reset();
                    }
                    if (!valid) {
                        log.warn("Watch key is no longer valid, removing directory: {}", directories.get(key).directory);
                        directories.remove(key);
                        if(directories.isEmpty()) {
                            stop();
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @AllArgsConstructor
    @Getter
    private static class MonitorVo {
        private Path directory;
        private Callback<Path, FileEventType> callback;
    }
}
