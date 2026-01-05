package pe.kr.thekey.framework.core.utils.file;

import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.core.utils.Callback;

import java.io.File;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WatcherForFile {
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);

    /**
     * Registers file for monitoring with provided callback
     */
    public void register(File file, Callback<File, FileEventType> callback) {
        // Schedules file monitoring; invokes callback on change
        if (file != null && file.exists() && file.isFile()) {
            executor.scheduleWithFixedDelay(new Monitor(file) {
                @Override
                public void onChange(File file) {
                    callback.accept(file, FileEventType.MODIFY);
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
        else {
            log.warn("File does not exist or is not a regular file: {}", file);
        }
    }

    public void register(String file, Callback<File, FileEventType> callback) {
        register(new File(file), callback);
    }

    private abstract static class Monitor extends TimerTask {
        private long timestamp;
        private final File file;

        private Monitor(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            long timestamp = file.lastModified();
            if (this.timestamp != timestamp) {
                this.timestamp = timestamp;
                this.onChange(this.file);
            }
        }

        public abstract void onChange(File file);
    }
}
