package org.anotherkyle.commonlib.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.util.Assert;

import java.util.ArrayList;

public class JsonUtil {
    public static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static String parseToJsonString(Object o) {
        Assert.notNull(o, "Null input is not allowed");
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException("Parse object to JSON failed", e);
        }
    }

    public static JsonNode parseToJsonNode(String s) throws RuntimeException {
        return parseToTargetClass(s, JsonNode.class);
    }

    public static JsonNode parseToJsonNode(Object o) throws RuntimeException {
        return parseToTargetClass(parseToJsonString(o), JsonNode.class);
    }

    public static Object parseToObject(String s) throws RuntimeException {
        return parseToTargetClass(s, Object.class);
    }

    public static <T> T parseToTargetClass(String s, Class<T> targetCls) {
        Assert.notNull(s, "Null input is not allowed");
        try {
            return objectMapper.readValue(s, targetCls);
        } catch (Exception e) {
            throw new RuntimeException("Parse JSON to TargetClass failed", e);
        }
    }

    public static <T> T parseToTargetClass(String s, TypeReference<T> typeReference) {
        Assert.notNull(s, "Null input is not allowed");
        try {
            return objectMapper.readValue(s, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Parse JSON to TargetClass failed", e);
        }
    }

    public static JsonNode wrapPureText(String s) {
        ArrayList<String> tmp = new ArrayList<String>();
        tmp.add(s);
        return JsonUtil.parseToJsonNode(JsonUtil.parseToJsonString(tmp));
    }

    public static <T> T treeToValue(JsonNode jnode, Class<T> pojoType) {
        try {
            return objectMapper.treeToValue(jnode, pojoType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parse JSON to TargetClass failed", e);
        }
    }

    public static JsonNode valueToTree(Object o) {
        return objectMapper.valueToTree(o);
    }
}
