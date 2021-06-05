package me.xemor.superheroes2.data.storage;

import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.data.SuperheroPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class YAMLStorage implements Storage {

    private Superheroes2 superheroes2;
    private final YamlConfiguration currentDataYAML;
    private File currentDataFile;
    private HeroHandler heroHandler;

    public YAMLStorage(HeroHandler heroHandler) {
        this.superheroes2 = heroHandler.getPlugin();
        this.heroHandler = heroHandler;
        currentDataFile = new File(superheroes2.getDataFolder(), "data.yml");
        try {
            currentDataFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentDataYAML = YamlConfiguration.loadConfiguration(currentDataFile);
    }

    private ConfigurationSection getSection(UUID uuid) {
        return currentDataYAML.getConfigurationSection(String.valueOf(uuid));
    }

    @Override
    public void saveSuperheroPlayer(SuperheroPlayer superheroPlayer) {
        ConfigurationSection section = getSection(superheroPlayer.getUUID());
        if (section == null) {
            section = currentDataYAML.createSection(superheroPlayer.getUUID().toString());
        }
        section.set("hero", superheroPlayer.getSuperhero().getName());
        section.set("hero_cmd_timestamp", superheroPlayer.getHeroCommandTimestamp());
        currentDataYAML.set(String.valueOf(superheroPlayer.getUUID()), section);
        Bukkit.getScheduler().runTaskAsynchronously(superheroes2, () -> {
            synchronized (currentDataYAML) {
                try {
                    currentDataYAML.save(currentDataFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public SuperheroPlayer loadSuperheroPlayer(UUID uuid) {
        ConfigurationSection section = getSection(uuid);
        if (section == null) {
            return null;
        }
        Superhero superhero = heroHandler.getSuperhero(section.getString("hero", "NOPOWER"));
        long heroCommandTimestamp = section.getLong("hero_cmd_timestamp", 0);
        return new SuperheroPlayer(uuid, superhero, heroCommandTimestamp);
    }

    @Override
    public CompletableFuture<SuperheroPlayer> loadSuperheroPlayerAsync(@NotNull UUID uuid) {
        CompletableFuture<SuperheroPlayer> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(superheroes2, () -> future.complete(loadSuperheroPlayer(uuid)));
        return future;
    }
}
