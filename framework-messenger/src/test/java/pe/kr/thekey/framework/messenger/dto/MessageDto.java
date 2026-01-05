package pe.kr.thekey.framework.messenger.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class MessageDto {
    private Map<String, Object> header = new LinkedHashMap<>();
    private Map<String, Object> data = new LinkedHashMap<>();
    private Map<String, Object> footer = new LinkedHashMap<>();
}