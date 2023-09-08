package me.xemor.superheroes.sentry;

import io.sentry.Sentry;
import org.bukkit.plugin.java.JavaPlugin;

public class SentryInitializer {

    public static void initSentry(String dsn, JavaPlugin plugin) {
        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setDebug(false);
            options.setShutdownTimeoutMillis(5000);
        });
        SentryLogHandler logHandler = new SentryLogHandler(plugin);
        plugin.getLogger().getParent().addHandler(logHandler);
    }

}

