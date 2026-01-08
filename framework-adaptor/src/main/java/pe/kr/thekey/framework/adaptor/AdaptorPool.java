package pe.kr.thekey.framework.adaptor;

import org.apache.commons.io.IOUtils;
import pe.kr.thekey.framework.adaptor.exception.ExternalException;
import pe.kr.thekey.framework.core.config.ErrorCode;

import java.io.*;

public interface AdaptorPool {
    byte[] execute(String uuid, String channelId, byte[] data) throws ExternalException;

    default <T> T execute(String uuid, String channelId, Object data, Class<T> responseType) throws ExternalException {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        ) {
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();

            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byte[] execute = execute(uuid, channelId, byteArray);
            ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(execute));
            return (T) objectInput.readObject();

        } catch (IOException ex) {
            throw new ExternalException(ErrorCode.FEX018, ex);
        } catch (ClassNotFoundException ex) {
            throw new ExternalException(ErrorCode.FEX019, ex);
        }
    }
}
