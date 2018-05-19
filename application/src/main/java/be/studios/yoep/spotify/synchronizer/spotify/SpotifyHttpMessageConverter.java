package be.studios.yoep.spotify.synchronizer.spotify;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;

@Log4j2
public class SpotifyHttpMessageConverter extends MappingJackson2HttpMessageConverter {
    public SpotifyHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try {
            ByteArrayOutputStream inputStream = new ByteArrayOutputStream(inputMessage.getBody().available());
            IOUtils.copy(inputMessage.getBody(), inputStream);
//            log.debug(inputStream.toString());
            return super.read(type, contextClass, new MappingJacksonInputMessage(
                    new ByteArrayInputStream(inputStream.toByteArray()), inputMessage.getHeaders()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
