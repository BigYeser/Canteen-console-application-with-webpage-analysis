package de.uniwue.jpp.mensabot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniwue.jpp.errorhandling.OptionalWithMessage;

public class JsonUtil {

    public static <T> OptionalWithMessage<T> stringToObject(String jsonString, Class<T> objectType)  {
        try {
            return OptionalWithMessage.of(new ObjectMapper().readValue(jsonString,objectType));
        } catch (JsonProcessingException e) {
            return OptionalWithMessage.ofMsg(e.getMessage());
        }
    }

    public static <T> OptionalWithMessage<String> objectToString(T object){
        try {
            return OptionalWithMessage.of(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object));
        } catch (JsonProcessingException e) {
            return OptionalWithMessage.ofMsg(e.getMessage());
        }
    }
}