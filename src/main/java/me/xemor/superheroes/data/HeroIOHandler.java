package me.xemor.superheroes.data;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.storage.MySQLStorage;
import me.xemor.superheroes.data.storage.Storage;
import me.xemor.superheroes.data.storage.YAMLStorage;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;

public class HeroIOHandler {

    final ExecutorService threads;
    @Nullable
    private Storage storage;
    private static final Object POISON = new Object();
    private final BlockingQueue<Object> loadingPlayerQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Object> savingPlayerQueue = new LinkedBlockingQueue<>();
    private final ConfigHandler configHandler = Superheroes.getInstance().getConfigHandler();

    public HeroIOHandler() {
        threads = Executors.newFixedThreadPool(2);
        threads.submit(this::loadPlayersInQueue);
        threads.submit(this::savePlayersInQueue);
    }

    public void loadPlayersInQueue() {
        while (true) {
            try {
                if (storage == null) {
                    Superheroes.getInstance().getLogger().log(Level.FINE, "Waiting 1000ms for server to finish loading.");
                    Thread.sleep(1000);
                    continue;
                }
                Object object = loadingPlayerQueue.take();
                if (object == POISON) {
                    return;
                }
                LoadingPlayer player = (LoadingPlayer) object;
                SuperheroPlayer superheroPlayer = storage.loadSuperheroPlayer(player.getUUID());
                player.getFuture().complete(superheroPlayer);
            } catch (InterruptedException ignored) {}
        }
    }

    public void savePlayersInQueue() {
        while (true) {
            try {
                if (storage == null) {
                    Superheroes.getInstance().getLogger().fine("Storage is null... waiting 1s for server to finish loading.");
                    Thread.sleep(1000);
                    continue;
                }
                Object object = savingPlayerQueue.take();
                if (object == POISON) {
                    return;
                }
                SavingPlayer player = (SavingPlayer) object;
                storage.saveSuperheroPlayer(player.getSuperheroPlayer());
                player.getFuture().complete(new Object());
            } catch (InterruptedException ignored) {}
        }
    }

    public CompletableFuture<SuperheroPlayer> loadSuperHeroPlayerAsync(UUID uuid) {
        LoadingPlayer loadingPlayer = new LoadingPlayer(uuid);
        try {
            loadingPlayerQueue.put(loadingPlayer);
        } catch (InterruptedException e) { e.printStackTrace(); }
        return loadingPlayer.getFuture();
    }

    public void saveSuperheroPlayerAsync(SuperheroPlayer superheroPlayer) {
        SavingPlayer savingPlayer = new SavingPlayer(superheroPlayer);
        try {
            savingPlayerQueue.put(savingPlayer);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public void handlePlayerData() {
        String databaseType = ConfigHandler.getDatabaseYAML().database().getType();
        if (databaseType.equalsIgnoreCase("YAML")) {
            storage = new YAMLStorage();
        } else if (databaseType.equalsIgnoreCase("MySQL")) {
            storage = new MySQLStorage();
        } else {
            Bukkit.getLogger().severe("Invalid database type specified!");
        }
    }

    public CompletableFuture<Void> importFiles() {
        YAMLStorage yamlStorage = new YAMLStorage();
        List<SuperheroPlayer> superheroPlayers = yamlStorage.exportSuperheroPlayers();
        return storage.importSuperheroPlayers(superheroPlayers);
    }

    public void exportFiles() {
        List<SuperheroPlayer> superheroPlayers = storage.exportSuperheroPlayers();
        YAMLStorage yamlStorage = new YAMLStorage();
        yamlStorage.importSuperheroPlayers(superheroPlayers);
    }

    public void shutdown() {
        loadingPlayerQueue.add(POISON);
        savingPlayerQueue.add(POISON);
        threads.shutdown();
        try {
            threads.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class LoadingPlayer {
        private final UUID uuid;
        private final CompletableFuture<SuperheroPlayer> future = new CompletableFuture<>();

        public LoadingPlayer(UUID uuid) {
            this.uuid = uuid;
        }

        public UUID getUUID() {
            return uuid;
        }

        public CompletableFuture<SuperheroPlayer> getFuture() {
            return future;
        }
    }

    static class SavingPlayer {
        private final SuperheroPlayer superheroPlayer;
        private final CompletableFuture<Object> future = new CompletableFuture<>();

        public SavingPlayer(SuperheroPlayer superheroPlayer) {
            this.superheroPlayer = superheroPlayer;
        }

        public SuperheroPlayer getSuperheroPlayer() {
            return superheroPlayer;
        }

        public CompletableFuture<Object> getFuture() {
            return future;
        }
    }

}
