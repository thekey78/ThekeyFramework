package pe.kr.thekey.framework.core.utils.file;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public enum FileEventType {
    CREATE, DELETE, MODIFY, OVERFLOW;

    static FileEventType getEventType(WatchEvent.Kind<?> eventKind) {
        if (eventKind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
            return CREATE;
        } else if (eventKind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
            return MODIFY;
        } else if (eventKind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
            return DELETE;
        }
        return OVERFLOW;
    }
}
