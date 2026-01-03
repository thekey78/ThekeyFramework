package pe.kr.thekey.framework.core.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import pe.kr.thekey.framework.core.utils.file.Callback;
import pe.kr.thekey.framework.core.utils.file.FileEventType;
import pe.kr.thekey.framework.core.utils.file.WatcherForDirectory;
import pe.kr.thekey.framework.core.utils.file.WatcherForFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static pe.kr.thekey.framework.core.utils.ApplicationContextHolder.*;

@Slf4j
@Service
@AllArgsConstructor
public class ResourceUtils extends org.springframework.util.ResourceUtils {
    public static Path getPath(String resourceLocation) throws FileNotFoundException {
        return getFile(resourceLocation).toPath();
    }

    public static Path getPath(URL resource, String description) throws FileNotFoundException {
        return getFile(resource, description).toPath();
    }

    public void watchDirectory(Path directory, Callback<Path, FileEventType> callback) throws IOException {
        ApplicationContext applicationContext = getApplicationContext();
        WatcherForDirectory directoryWatcher = applicationContext.getBean(WatcherForDirectory.class);
        directoryWatcher.register(directory, callback);
    }

    public void watchFile(File file, Callback<File, FileEventType> callback) {
        ApplicationContext applicationContext = getApplicationContext();
        WatcherForFile fileWatcher = applicationContext.getBean(WatcherForFile.class);
        fileWatcher.register(file, callback);
    }
}
