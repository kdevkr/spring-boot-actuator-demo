package kr.kdev.demo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.SanitizableData;
import org.springframework.boot.actuate.endpoint.SanitizingFunction;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ActuatorSanitizingFunction implements SanitizingFunction, InitializingBean {
    private static final String[] REGEX_PARTS = {"*", "$", "^", "+"};

    private static final Set<String> DEFAULT_KEYS_TO_SANITIZE = new LinkedHashSet<>(
            Arrays.asList("password", "secret", "key", "token", ".*credentials.*", "vcap_services",
                    "^vcap\\.services.*$", "sun.java.command", "^spring[._]application[._]json$"));

    private static final Set<String> URI_USERINFO_KEYS = new LinkedHashSet<>(
            Arrays.asList("uri", "uris", "url", "urls", "address", "addresses"));

    private static final Pattern URI_USERINFO_PATTERN = Pattern
            .compile("^\\[?[A-Za-z][A-Za-z0-9\\+\\.\\-]+://.+:(.*)@.+$");

    private final List<Pattern> keysToSanitize = new ArrayList<>();

    @Value("${management.endpoint.additionalKeysToSanitize:}")
    private List<String> additionalKeysToSanitize;

    static {
        DEFAULT_KEYS_TO_SANITIZE.addAll(URI_USERINFO_KEYS);
    }

    private Pattern getPattern(String value) {
        if (isRegex(value)) {
            return Pattern.compile(value, Pattern.CASE_INSENSITIVE);
        }
        return Pattern.compile(".*" + value + "$", Pattern.CASE_INSENSITIVE);
    }

    private boolean isRegex(String value) {
        for (String part : REGEX_PARTS) {
            if (value.contains(part)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SanitizableData apply(SanitizableData data) {
        if (data.getValue() == null) {
            return data;
        }

        for (Pattern pattern : keysToSanitize) {
            if (pattern.matcher(data.getKey()).matches()) {
                if (keyIsUriWithUserInfo(pattern)) {
                    return data.withValue(sanitizeUris(data.getValue().toString()));
                }

                return data.withValue(SanitizableData.SANITIZED_VALUE);
            }
        }

        return data;
    }

    private void addKeysToSanitize(Collection<String> keysToSanitize) {
        for (String key : keysToSanitize) {
            this.keysToSanitize.add(getPattern(key));
        }
    }

    private boolean keyIsUriWithUserInfo(Pattern pattern) {
        for (String uriKey : URI_USERINFO_KEYS) {
            if (pattern.matcher(uriKey).matches()) {
                return true;
            }
        }
        return false;
    }

    private Object sanitizeUris(String value) {
        return Arrays.stream(value.split(",")).map(this::sanitizeUri).collect(Collectors.joining(","));
    }

    private String sanitizeUri(String value) {
        Matcher matcher = URI_USERINFO_PATTERN.matcher(value);
        String password = matcher.matches() ? matcher.group(1) : null;
        if (password != null) {
            return StringUtils.replace(value, ":" + password + "@", ":" + SanitizableData.SANITIZED_VALUE + "@");
        }
        return value;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        addKeysToSanitize(DEFAULT_KEYS_TO_SANITIZE);
        addKeysToSanitize(URI_USERINFO_KEYS);
        addKeysToSanitize(additionalKeysToSanitize);
    }
}
