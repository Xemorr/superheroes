package me.xemor.superheroes.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.sentry.util.FileUtils;
import me.xemor.configurationdata.ConfigurationData;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.config.ConfigYaml;
import me.xemor.superheroes.events.SuperheroLoadEvent;
import me.xemor.superheroes.events.SuperheroesReloadEvent;
import me.xemor.superheroes.language.LanguageYaml;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.spell.Spells;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigHandler {
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build();
    private static LanguageYaml languageYAML;
    private static DatabaseYaml databaseYAML;
    private static ConfigYaml configYAML;
    private File dataFolder;
    private final Superheroes superheroes;

    public ConfigHandler(Superheroes superheroes) {
        this.superheroes = superheroes;
        superheroes.saveDefaultConfig();
        dataFolder = superheroes.getDataFolder();
        if (!new File(superheroes.getDataFolder(), "language.yml").exists()) {
            superheroes.saveResource("language.yml", false);
        }
        if (!new File(superheroes.getDataFolder(), "database.yml").exists()) {
            superheroes.saveResource("database.yml", false);
        }

        loadConfigs();

        this.handleSuperpowersFolder();
    }

    public void reloadConfig(HeroHandler heroHandler) {
        SuperheroesReloadEvent superheroesReloadEvent = new SuperheroesReloadEvent();
        Bukkit.getServer().getPluginManager().callEvent(superheroesReloadEvent);
        loadConfigs();
        heroHandler.loadConfigItems();
        this.handleSuperpowersFolder();
        heroHandler.handlePlayerData();
        this.loadSuperheroes(heroHandler);
        heroHandler.setHeroesIntoMemory(new HashMap<>());
        for (Player player : Bukkit.getOnlinePlayers()) {
            heroHandler.loadSuperheroPlayer(player);
        }
    }

    public void loadConfigs() {
        try {
            ObjectMapper objectMapper = setupObjectMapper();
            databaseYAML = objectMapper.readValue(new File(dataFolder, "database.yml"), DatabaseYaml.class);
            languageYAML = objectMapper.readValue(new File(dataFolder, "language.yml"), LanguageYaml.class);
            configYAML = objectMapper.readValue(new File(dataFolder, "config.yml"), ConfigYaml.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleSuperpowersFolder() {
        File powersFolder = this.getPowersFolder();
        if (powersFolder.mkdir()) {
            try {
                Path myPath;
                URI powers;
                FileSystem fileSystem = null;
                try {
                    powers = this.getClass().getClassLoader().getResource("powers").toURI();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    return;
                }
                if (powers.getScheme().equals("jar")) {
                    fileSystem = FileSystems.newFileSystem(powers, Collections.emptyMap());
                    myPath = fileSystem.getPath("powers");
                } else {
                    myPath = Paths.get(powers);
                }
                Stream<Path> walk = Files.walk(myPath, 1);
                Iterator<Path> it = walk.iterator();
                it.next();
                while (it.hasNext()) {
                    String path = it.next().toString();
                    if (path.charAt(0) == '/') {
                        path = path.substring(1);
                    }
                    this.superheroes.saveResource(path, false);
                }
                if (fileSystem != null) fileSystem.close();
                walk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getPowersFolder() {
        return new File(this.getDataFolder(), "powers");
    }

    public ObjectMapper setupObjectMapper() {
        ObjectMapper objectMapper = ConfigurationData.setupObjectMapperForConfigurationData(new ObjectMapper(new YAMLFactory()));;
        if (superheroes.hasSkillsLibrary()) objectMapper = SkillsLibrary.getInstance().setupObjectMapper(objectMapper);
        objectMapper.registerSubtypes(Skill.getNamedTypes());
        objectMapper.registerSubtypes(Spells.getNamedTypes());
        return objectMapper;
    }

    public void loadSuperheroes(HeroHandler heroHandler) {
        ObjectMapper objectMapper = setupObjectMapper();
        Map<String, Superhero> nameToSuperhero = Arrays.stream(getPowersFolder().listFiles())
                .parallel()
                .map((file) -> {
                    try (InputStream is = new FileInputStream(file)) {
                        return objectMapper.readValue(is, Superhero.class);
                    } catch (IOException e) {
                        Superheroes.getInstance().getLogger().severe("While parsing file: %s\n%s".formatted(file.getName(), e));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList()
                .stream()
                .map((hero) -> {
                    SuperheroLoadEvent superheroLoadEvent = new SuperheroLoadEvent(hero);
                    Bukkit.getServer().getPluginManager().callEvent(superheroLoadEvent);
                    if (superheroLoadEvent.isCancelled()) return null;
                    return hero;
                })
                .filter(Objects::nonNull)
                .filter((it) -> it.getName() != null)
                .collect(Collectors.toMap((hero) -> hero.getName().toLowerCase(), (hero) -> hero));
        heroHandler.registerHeroes(nameToSuperhero);
    }

    private File getDataFolder() {
        return Superheroes.getInstance().getDataFolder();
    }

    public Superhero getDefaultHero() {
        return configYAML.defaultHero();
    }

    public static LanguageYaml getLanguageYAML() {
        return languageYAML;
    }

    public static DatabaseYaml getDatabaseYAML() {
        return databaseYAML;
    }

    public static ConfigYaml getConfigYAML() {
        return configYAML;
    }
}

