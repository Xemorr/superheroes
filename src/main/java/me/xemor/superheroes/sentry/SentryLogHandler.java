package me.xemor.superheroes.sentry;

import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Message;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;


public class SentryLogHandler extends ConsoleHandler {
    private final JavaPlugin plugin;

    public SentryLogHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void publish(LogRecord record) {
        Throwable exception = record.getThrown();
        if (exception == null) return;
        boolean hasSuperheroesError = false;
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length == 0) return;
        for (int i = 0; i < 4; i++) {
            StackTraceElement e = stackTrace[i];
            if (e.toString().contains("me.xemor.superheroes")) {
                hasSuperheroesError = true;
                break;
            }
        }
        if (!hasSuperheroesError) return;
        String message = getFormatter().formatMessage(record);
        SentryEvent event = new SentryEvent(exception);
        event.setLevel(SentryLevel.ERROR);
        Message sentryMessage = new Message();
        sentryMessage.setMessage(message);
        event.setMessage(sentryMessage);
        event.setServerName(plugin.getServer().getName());
        event.setTag("ServerVersion", plugin.getServer().getVersion());
        event.setTag("ServerImplementation", plugin.getServer().getBukkitVersion());
        event.setTag("Version", plugin.getDescription().getVersion());
        StringBuilder systemName = new StringBuilder();
        systemName.append(System.getProperty("os.name"));
        systemName.append(" ");
        systemName.append(System.getProperty("os.version"));
        systemName.append(" ");
        systemName.append(System.getProperty("os.arch"));
        event.setEnvironment(systemName.toString());
        Sentry.captureEvent(event);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}