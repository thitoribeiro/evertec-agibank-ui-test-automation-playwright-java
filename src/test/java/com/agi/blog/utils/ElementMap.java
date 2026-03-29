package com.agi.blog.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ElementMap {

    private final Properties props;
    private final String pageName;

    public ElementMap(String pageName) {
        this.pageName = pageName;
        this.props = new Properties();
        String path = "selectors/" + pageName + ".properties";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException(
                    "Selector file not found on classpath: " + path);
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to load selector file: " + path, e);
        }
    }

    public String get(String key) {
        return getFallbacks(key).get(0);
    }

    public String get(String key, String arg) {
        return getFallbacks(key, arg).get(0);
    }

    public List<String> getFallbacks(String key) {
        String raw = props.getProperty(key);
        if (raw == null) {
            throw new RuntimeException(
                "Selector key '" + key + "' not found in " + pageName + ".properties");
        }
        return Arrays.stream(raw.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public List<String> getFallbacks(String key, String arg) {
        return getFallbacks(key).stream()
                .map(s -> s.replace("%s", arg))
                .collect(Collectors.toList());
    }

    public String getAsComma(String key) {
        return String.join(", ", getFallbacks(key));
    }
}
