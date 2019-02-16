package cn.ktchen.landlords.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static ObjectMapper getMapper() {
        if (mapper == null){
            mapper = new ObjectMapper();
        }
        return mapper;
    }

    private static ObjectMapper mapper;

    public static String decode(Object object) {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
