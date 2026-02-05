package io.cronx.toolkit.utils;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            String msg = "write map to json string error.msg:" + ExceptionUtils.getRootCauseMessage(e);
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    public static <T> T readJson(String message, Class<T> mapClass) {
        try {
            return objectMapper.readerFor(mapClass).readValue(message);
        } catch (Exception e) {
            String msg = "read form json string error.msg:" + ExceptionUtils.getRootCauseMessage(e);
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    @SneakyThrows
    public static String toJson(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }

    @SneakyThrows
    public static String toJsonIgnoreAnnotation(Object obj) {
        ObjectMapper ignoreAnnotationMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
        return ignoreAnnotationMapper.writeValueAsString(obj);
    }

    @SneakyThrows
    public static Object toObj(String jsonStr, Class clz) {
        return objectMapper.readValue(jsonStr, clz);
    }

    @SneakyThrows
    public static String toPrettyJson(Object obj) {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}
