package me.xemor.superheroes2.data;

import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.storage.LegacyStorage;
import me.xemor.superheroes2.data.storage.MySQLStorage;
import me.xemor.superheroes2.data.storage.Storage;
import me.xemor.superheroes2.data.storage.YAMLStorage;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

public class HeroIOHandler {

    private Storage storage;
    private final ReentrantLock loadingPlayerLock = new ReentrantLock();
    private final Deque<LoadingPlayer> loadingPlayerQueue = new ArrayDeque<>();
    private final ReentrantLock savingPlayerLock = new ReentrantLock();
    private final Deque<SavingPlayer> savingPlayerQueue = new ArrayDeque<>();
    private final ConfigHandler configHandler = Superheroes2.getInstance().getConfigHandler();

    public HeroIOHandler() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Superheroes2.getInstance(), this::loadPlayersInQueue, 1L, 1L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Superheroes2.getInstance(), this::savePlayersInQueue, 1L, 1L);
    }

    public void loadPlayersInQueue() {
        final Deque<LoadingPlayer> loadingPlayerQueue;
        try {
            loadingPlayerLock.lock();
            loadingPlayerQueue = new ArrayDeque<>(this.loadingPlayerQueue);
            this.loadingPlayerQueue.clear();
        } finally {
            loadingPlayerLock.unlock();
        }
        for (LoadingPlayer loadingPlayer : loadingPlayerQueue) {
            SuperheroPlayer superheroPlayer = storage.loadSuperheroPlayer(loadingPlayer.getUUID());
            loadingPlayer.getFuture().complete(superheroPlayer);
        }
    }

    public void savePlayersInQueue() {
        final Deque<SavingPlayer> savingPlayerQueue;
        try {
            savingPlayerLock.lock();
            savingPlayerQueue = new ArrayDeque<>(this.savingPlayerQueue);
            this.savingPlayerQueue.clear();
        } finally {
            savingPlayerLock.unlock();
        }
        for (SavingPlayer savingPlayer : savingPlayerQueue) {
            storage.saveSuperheroPlayer(savingPlayer.getSuperheroPlayer());
            savingPlayer.getFuture().complete(new Object());
        }
    }

    public CompletableFuture<SuperheroPlayer> loadSuperHeroPlayerAsync(UUID uuid) {
        LoadingPlayer loadingPlayer = new LoadingPlayer(uuid);
        try {
            loadingPlayerLock.lock();
            loadingPlayerQueue.add(loadingPlayer);
        } finally {
            loadingPlayerLock.unlock();
        }
        return loadingPlayer.getFuture();
    }

    public CompletableFuture<Object> saveSuperheroPlayerAsync(SuperheroPlayer superheroPlayer) {
        SavingPlayer savingPlayer = new SavingPlayer(superheroPlayer);
        try {
            savingPlayerLock.lock();
            savingPlayerQueue.add(savingPlayer);
        } finally {
            savingPlayerLock.unlock();
        }
        return savingPlayer.getFuture();
    }

    public void handlePlayerData() {
        String databaseType = configHandler.getDatabaseType();
        if (databaseType.equalsIgnoreCase("LEGACY")) {
            LegacyStorage legacy = new LegacyStorage();
            List<SuperheroPlayer> values = legacy.exportSuperheroPlayers();
            File file = legacy.getCurrentDataFile();
            file.renameTo(new File(Superheroes2.getInstance().getDataFolder(), "old_data.yml"));
            storage = new YAMLStorage();
            for (SuperheroPlayer superheroPlayer: values) {
                storage.saveSuperheroPlayer(superheroPlayer);
            }
            configHandler.setDatabaseType("YAML");
        }
        else if (databaseType.equalsIgnoreCase("YAML")) {
            storage = new YAMLStorage();
        }
        else if (databaseType.equalsIgnoreCase("MySQL")) {
            storage = new MySQLStorage();
        }
        else {
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

    class LoadingPlayer {
        private UUID uuid;
        private CompletableFuture<SuperheroPlayer> future = new CompletableFuture<>();;

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

    class SavingPlayer {
        private SuperheroPlayer superheroPlayer;
        private CompletableFuture<Object> future = new CompletableFuture<>();

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
