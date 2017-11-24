package com.example.service.patch;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A very simple representation of a patch+json object
 */
public class JSONPatchContainer {

    private static final Logger LOGGER = Logger.getLogger(JSONPatchContainer.class.getName());
    private String operation;
    private String path;
    private String value;
    private String[] pathElements;

    public JSONPatchContainer() {
        //default constructor required by Jackson
    }

    public JSONPatchContainer(String op, String path, String value) {
        this.operation = op;
        this.path = path;
        this.value = value;
    }

    @JsonProperty("op")
    public String getOperation() {
        return operation;
    }

    public String getPath() {
        return path;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{\"op\":\"" + operation + "\",\"path\":\"" + path + "\",\"value\":\"" + value + "\"}";
    }

    @JsonIgnore
    public boolean replaceValue(Object o) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        Class c = o.getClass();
        return replaceValue(c, o, 0);
    }

    private boolean replaceValue(Class c, Object o, int level) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        if (level < getPathElements().length) {
            Field f = c.getDeclaredField(getPathElements()[level]);
            boolean access = f.isAccessible();
            f.setAccessible(true);
            if (f.getType().equals(String.class) && level == getPathElements().length - 1) {
                f.set(o, value);
                if (!access) {
                    LOGGER.log(Level.WARNING,
                            "Patched private field immutabilty may be violated (Class::Name) ({0}::{1}) Object::{3}", new Object[]{c.getName(), f.getName(), o.toString()});
                }
                return true;
            }
            Object obj = f.get(o);
            f.setAccessible(access);
            return replaceValue(f.getType(), obj, ++level);
        }
        return false;
    }

    private String[] getPathElements() {
        if (pathElements != null) {
            return pathElements;
        }
        String[] elements = path.split("/");
        List<String> l = Arrays.asList(elements);
        List<String> r = l.stream()
                .filter(e -> !e.equals(""))
                .collect(Collectors.toList());
        String[] result = new String[r.size()];
        pathElements = r.toArray(result);
        return pathElements;
    }
}
