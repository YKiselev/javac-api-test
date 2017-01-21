package com.github.ykiselev.console;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class CommandProcessor {

    public interface CommandHandler {

        void handle(String[] args) throws Exception;
    }

    private final Pattern cmdPattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");

    private final Map<String, CommandHandler> handlers;

    private final CommandHandler defaultHandler;

    /**
     * @return immutable map of supported commands
     */
    public Map<String, CommandHandler> handlers() {
        return handlers;
    }

    public CommandProcessor(Map<String, CommandHandler> handlers, CommandHandler defaultHandler) {
        this.handlers = ImmutableMap.copyOf(handlers);
        this.defaultHandler = defaultHandler != null
                ? defaultHandler
                : this::defaultHandler;
    }

    private void defaultHandler(String[] args) {
        throw new IllegalArgumentException("Unknown command: " + args[0]);
    }

    private String[] split(String line) {
        final Matcher matcher = cmdPattern.matcher(line);
        final List<String> parts = new ArrayList<>();
        while (matcher.find()) {
            String tmp;
            if (matcher.group(1) != null) {
                tmp = matcher.group(1);
                if (tmp.startsWith("\"")) {
                    tmp = tmp.substring(1);
                }
                if (tmp.endsWith("\"")) {
                    tmp = tmp.substring(0, tmp.length() - 1);
                }
                parts.add(tmp);
            } else {
                tmp = matcher.group(2);
            }
            if (StringUtils.isNotBlank(tmp)) {
                parts.add(tmp);
            }
        }
        return parts.toArray(new String[0]);
    }

    public void execute(String line) throws Exception {
        final String[] args = split(line);
        if (args.length > 0) {
            ObjectUtils.firstNonNull(
                    handlers.get(args[0]),
                    defaultHandler
            ).handle(args);
        }
    }
}
