package pe.kr.thekey.framework.adaptor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.messenger.service.MessengerService;

import java.nio.charset.Charset;

@Slf4j
@RequiredArgsConstructor
public class AdaptorConverter {
    private final MessengerService messengerService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    public byte[] convertToBytes(Object data, AdaptorProperties.DataType dataType, String encoding) throws Exception {
        if (data instanceof byte[]) {
            return (byte[]) data;
        }

        String result;
        switch (dataType) {
            case JSON:
                result = objectMapper.writeValueAsString(data);
                break;
            case XML:
                result = xmlMapper.writeValueAsString(data);
                break;
            case DELIMITED:
                if (messengerService != null) {
                    result = messengerService.marshal(data.getClass().getSimpleName(), data);
                } else {
                    result = data.toString();
                }
                break;
            case BINARY:
                if (data instanceof String) {
                    result = (String) data;
                } else {
                    // 기본적으로 toString() 사용 또는 별도 처리
                    result = data.toString();
                }
                break;
            default:
                result = data.toString();
        }

        return result.getBytes(Charset.forName(encoding));
    }

    public Object convertFromBytes(byte[] data, Class<?> targetClass, AdaptorProperties.DataType dataType, String encoding) throws Exception {
        if (targetClass == byte[].class) {
            return data;
        }

        String content = new String(data, Charset.forName(encoding));
        switch (dataType) {
            case JSON:
                return objectMapper.readValue(content, targetClass);
            case XML:
                return xmlMapper.readValue(content, targetClass);
            case DELIMITED:
                if (messengerService != null) {
                    return messengerService.parse(targetClass.getSimpleName(), content);
                }
                return content;
            case BINARY:
                // Binary의 경우 Class 타입에 따라 변환이 필요할 수 있으나 기본은 String 처리
                if (targetClass == String.class) {
                    return content;
                }
                return content; // 임시
            default:
                return content;
        }
    }
}
