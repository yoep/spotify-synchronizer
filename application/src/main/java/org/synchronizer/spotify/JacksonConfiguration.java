package org.synchronizer.spotify;

import org.synchronizer.spotify.common.SynchronizerModule;
import org.synchronizer.spotify.spotify.SpotifyHttpMessageConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.LocalDate;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

@Configuration
public class JacksonConfiguration {
    @Bean
    public Module javaTimeModule() {
        return new JavaTimeModule()
                .addSerializer(LocalDate.class, new LocalDateSerializer(ofPattern("yyyy-MM-dd")))
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(ofPattern("yyyy-MM-dd")));
    }

    @Bean
    public Module jdk8Module() {
        return new Jdk8Module();
    }

    @Bean
    public Module synchronizerModule() {
        return new SynchronizerModule();
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(List<Module> modules) {
        return new Jackson2ObjectMapperBuilder()
                .modules(modules)
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .featuresToEnable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
                .featuresToEnable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
                .featuresToEnable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        return new SpotifyHttpMessageConverter(jackson2ObjectMapperBuilder.build());
    }
}
