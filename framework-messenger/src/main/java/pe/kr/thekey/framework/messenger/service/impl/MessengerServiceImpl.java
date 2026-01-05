package pe.kr.thekey.framework.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.beanio.BeanReader;
import org.beanio.BeanWriter;
import org.beanio.StreamFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pe.kr.thekey.framework.core.utils.file.WatcherForDirectory;
import pe.kr.thekey.framework.messenger.service.MessengerService;
import pe.kr.thekey.framework.messenger.util.MessengerProperties;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class MessengerServiceImpl implements MessengerService {

    private final MessengerProperties properties;

    private final WatcherForDirectory directoryWatcher;

    private StreamFactory factory;

    @PostConstruct
    @Override
    public void init() {
        loadMappings();
        if (properties.isFileChangeOnLoad()) {
            startWatchService();
        }
    }

    @PreDestroy
    @Override
    public void destroy() {
        if (properties.isFileChangeOnLoad()) {
            stopWatchService();
        }
    }

    @Synchronized
    public void loadMappings() {
        log.info("Loading BeanIO mappings...");
        StreamFactory newFactory = StreamFactory.newInstance();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // Loads BeanIO mappings from file resources
        try {
            List<String> mappings = properties.getMapping();
            if (mappings != null && !mappings.isEmpty()) {
                for (String filePath : mappings) {
                    Resource[] resources = resolver.getResources(filePath);
                    // Iterates resources; loads each mapping into stream factory
                    for (Resource resource : resources) {
                        if (!resource.exists()) {
                            log.warn("Mapping file not found: {}", resource.getDescription());
                            continue;
                        }
                        log.info("Loading mapping file: {}", resource.getFile().getAbsolutePath());
                        try (InputStream is = resource.getInputStream()) {
                            newFactory.load(is);
                        }
                    }
                }
            }
            this.factory = newFactory;
            log.info("BeanIO mappings loaded successfully.");
        } catch (IOException e) {
            log.error("Failed to load BeanIO mapping files", e);
            throw new RuntimeException("Failed to load BeanIO mapping files", e);
        }
    }

    /**
     * Initializes directory watch service for mapping files
     */
    @SneakyThrows
    public void startWatchService() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<String> mappings = properties.getMapping();
        Set<String> watchedDirectories = new HashSet<>();
        if (mappings != null) {
            // Extracts watched directories from mapping file resources
            for (String filePath : mappings) {
                Resource[] resources = resolver.getResources(filePath);
                for (Resource resource : resources) {
                    if (resource.exists()) {
                        watchedDirectories.add(resource.getFile().getParent());
                    }
                }
            }
        }

        log.info("Starting directory watchers for {}", watchedDirectories);

        // Registers directory watchers; reloads mappings on change
        watchedDirectories.forEach(directory -> {
            try {
                directoryWatcher.register(Paths.get(directory), (path, eventType) -> {
                    log.info("Detected change in directory: {}, {}", eventType, path);
                    loadMappings();
                });
            } catch (IOException e) {
                log.error("Failed to register directory watcher for: {}", directory, e);
                throw new RuntimeException(e);
            }
        });
    }

    private void stopWatchService() {
        directoryWatcher.stop();
    }

    @Override
    public Object parse(String streamName, String message) {
        try (BeanReader reader = factory.createReader(streamName, new StringReader(message))) {
            return reader.read();
        } catch (Exception e) {
            log.error("Error parsing message for stream: {}", streamName, e);
            throw e;
        }
    }

    @Override
    public String marshal(String streamName, Object bean) {
        StringWriter writer = new StringWriter();
        try (BeanWriter beanWriter = factory.createWriter(streamName, writer)) {
            beanWriter.write(bean);
            beanWriter.flush();
            return writer.toString().substring(0, writer.toString().length() - System.lineSeparator().length());
        } catch (Exception e) {
            log.error("Error marshaling bean for stream: {}", streamName, e);
            throw e;
        }
    }

    /**
     * 외부 호출에 의한 갱신을 위한 메서드
     */
    @Override
    public void reload() {
        log.info("Reloading BeanIO mappings...");
        loadMappings();
    }
}
