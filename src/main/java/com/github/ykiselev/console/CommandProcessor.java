package com.github.ykiselev.console;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CommandProcessor {

    public interface CommandHandler {

        void handle(String[] args) throws Exception;
    }

    private final Pattern cmdPattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");

    private final Map<String, CommandHandler> handlers;


    public CommandProcessor(Map<String, CommandHandler> handlers) {
        this.handlers = ImmutableMap.copyOf(handlers);
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
            final CommandHandler handler = handlers.get(args[0]);
            if (handler != null) {
                handler.handle(args);
            } else {
                throw new IllegalArgumentException("Unknown command: " + args[0]);
            }
        }
    }
}