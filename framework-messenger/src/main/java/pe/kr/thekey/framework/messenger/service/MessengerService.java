package pe.kr.thekey.framework.messenger.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beanio.BeanReader;
import org.beanio.BeanWriter;
import org.beanio.StreamFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import pe.kr.thekey.framework.messenger.util.MessengerProperties;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessengerService {

    private final MessengerProperties properties;
    private StreamFactory factory;
    private WatchService watchService;
    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        loadMappings();
        startWatchService();
    }

    @PreDestroy
    public void destroy() {
        stopWatchService();
    }

    public synchronized void loadMappings() {
        log.info("Loading BeanIO mappings...");
        StreamFactory newFactory = StreamFactory.newInstance();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            List<MessengerProperties.Mapping> mappings = properties.getMapping();
            if (mappings != null && !mappings.isEmpty()) {
                for (MessengerProperties.Mapping mapping : mappings) {
                    Resource[] resources = resolver.getResources(mapping.getFile());
                    for (Resource resource : resources) {
                        log.info("Loading mapping file: {}", resource.getFilename());
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

    private void startWatchService() {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            this.executorService = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "messenger-watch-service");
                t.setDaemon(true);
                return t;
            });

            List<MessengerProperties.Mapping> mappings = properties.getMapping();
            if (mappings != null) {
                for (MessengerProperties.Mapping mapping : mappings) {
                    String filePath = mapping.getFile();
                    if (filePath.startsWith("file:")) {
                        Path path = Paths.get(filePath.substring(5)).getParent();
                        if (Files.exists(path)) {
                            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                            log.info("Watching directory: {}", path);
                        }
                    }
                }
            }

            executorService.execute(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            log.info("Detected change in mapping file. Reloading...");
                            loadMappings();
                        }
                        if (!key.reset()) {
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("Error in WatchService", e);
                }
            });

        } catch (IOException e) {
            log.error("Failed to start WatchService", e);
        }
    }

    private void stopWatchService() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.error("Failed to close WatchService", e);
            }
        }
    }

    public Object parse(String streamName, String message) {
        try (BeanReader reader = factory.createReader(streamName, new StringReader(message))) {
            return reader.read();
        } catch (Exception e) {
            log.error("Error parsing message for stream: {}", streamName, e);
            throw e;
        }
    }

    public String marshal(String streamName, Object bean) {
        StringWriter writer = new StringWriter();
        try (BeanWriter beanWriter = factory.createWriter(streamName, writer)) {
            beanWriter.write(bean);
            beanWriter.flush();
            return writer.toString();
        } catch (Exception e) {
            log.error("Error marshaling bean for stream: {}", streamName, e);
            throw e;
        }
    }

    /**
     * 외부 호출에 의한 갱신을 위한 메서드
     */
    public void reload() {
        loadMappings();
    }
}
