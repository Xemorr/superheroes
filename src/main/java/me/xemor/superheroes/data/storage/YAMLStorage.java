package me.xemor.superheroes.data.storage;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.data.SuperheroPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

public class YAMLStorage implements Storage {

    private Superheroes superheroes;
    private HeroHandler heroHandler;
    private final YamlConfiguration currentDataYAML;
    private File currentDataFile;
    private ReentrantLock yamlLock = new ReentrantLock();

    public YAMLStorage() {
        superheroes = Superheroes.getInstance();
        heroHandler = superheroes.getHeroHandler();
        currentDataFile = new File(superheroes.getDataFolder(), "data.yml");
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
        yamlLock.lock();
        try {
            ConfigurationSection section = getSection(superheroPlayer.getUUID());
            if (section == null) {
                section = currentDataYAML.createSection(String.valueOf(superheroPlayer.getUUID()));
            }
            section.set("hero", superheroPlayer.getSuperhero().getName());
            section.set("hero_cmd_timestamp", superheroPlayer.getHeroCommandTimestamp());
            currentDataYAML.set(String.valueOf(superheroPlayer.getUUID()), section);
            try {
                currentDataYAML.save(currentDataFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            yamlLock.unlock();
        }

    }

    @Override
    public SuperheroPlayer loadSuperheroPlayer(@NotNull UUID uuid) {
        yamlLock.lock();
        try {
            ConfigurationSection section = getSection(uuid);
            if (section == null) {
                return null;
            }
            String heroName = section.getString("hero", "NOPOWER");
            Superhero superhero = heroHandler.getSuperhero(heroName);
            if (superhero == null) {
                return null;
            }
            long heroCommandTimestamp = section.getLong("hero_cmd_timestamp", 0);
            return new SuperheroPlayer(uuid, superhero, heroCommandTimestamp);
        } finally {
            yamlLock.unlock();
        }
    }

    @Override
    public CompletableFuture<Void> importSuperheroPlayers(List<SuperheroPlayer> superheroPlayers) {
        currentDataFile.renameTo(new File(superheroes.getDataFolder(), "old_data.yml"));
        currentDataFile = new File(superheroes.getDataFolder(), "data.yml");
        for (SuperheroPlayer superheroPlayer : superheroPlayers) {
            saveSuperheroPlayer(superheroPlayer);
        }
        return CompletableFuture.allOf();
    }

    @Override
    public List<SuperheroPlayer> exportSuperheroPlayers() {
        List<SuperheroPlayer> list = new ArrayList<>();
        for (Map.Entry<String, Object> item : currentDataYAML.getValues(false).entrySet()) {
            if (item.getValue() instanceof ConfigurationSection) {
                UUID uuid = UUID.fromString(item.getKey());
                list.add(loadSuperheroPlayer(uuid));
            }
        }
        return list;
    }
}
