package me.xemor.superheroes.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.sentry.util.FileUtils;
import me.xemor.configurationdata.ConfigurationData;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.events.SuperheroLoadEvent;
import me.xemor.superheroes.events.SuperheroesReloadEvent;
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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigHandler {
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build();
    private FileConfiguration languageYAML;
    private FileConfiguration databaseYAML;
    private FileConfiguration config;
    private final Superheroes superheroes;

    public ConfigHandler(Superheroes superheroes) {
        this.superheroes = superheroes;
        superheroes.saveDefaultConfig();
        File dataFolder = superheroes.getDataFolder();
        this.config = superheroes.getConfig();
        if (!new File(superheroes.getDataFolder(), "language.yml").exists()) {
            superheroes.saveResource("language.yml", false);
        }
        if (!new File(superheroes.getDataFolder(), "database.yml").exists()) {
            superheroes.saveResource("database.yml", false);
        }
        this.languageYAML = YamlConfiguration.loadConfiguration(new File(dataFolder, "language.yml"));
        this.databaseYAML = YamlConfiguration.loadConfiguration(new File(dataFolder, "database.yml"));
        this.handleSuperpowersFolder();
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
                    try {
                        return objectMapper.readValue(file, Superhero.class);
                    } catch (IOException e) {
                        Superheroes.getInstance().getLogger().severe(e.getMessage());
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

    public void reloadConfig(HeroHandler heroHandler) {
        SuperheroesReloadEvent superheroesReloadEvent = new SuperheroesReloadEvent();
        Bukkit.getServer().getPluginManager().callEvent(superheroesReloadEvent);
        this.superheroes.reloadConfig();
        this.config = this.superheroes.getConfig();
        heroHandler.loadConfigItems();
        this.handleSuperpowersFolder();
        heroHandler.handlePlayerData();
        this.loadSuperheroes(heroHandler);
        this.languageYAML = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "language.yml"));
        this.databaseYAML = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "database.yml"));
        heroHandler.setHeroesIntoMemory(new HashMap<>());
        for (Player player : Bukkit.getOnlinePlayers()) {
            heroHandler.loadSuperheroPlayer(player);
        }
    }

    public void saveConfig() {
        Superheroes.getScheduling().asyncScheduler().run(this.superheroes::saveConfig);
    }

    private File getDataFolder() {
        return Superheroes.getInstance().getDataFolder();
    }

    public boolean isPowerOnStartEnabled() {
        return this.config.getBoolean("powerOnStart.isEnabled", false);
    }

    public boolean openGUIOnStart() {
        return this.config.getBoolean("gui.onStart", true);
    }

    public String getGUIName() {
        return this.languageYAML.getString("GUI.name", "Pick your hero!");
    }

    public boolean canCloseGUI() {
        return this.config.getBoolean("gui.canClose", false);
    }

    public List<String> getDisabledWorlds() {
        return this.config.getStringList("disabledWorlds");
    }

    public boolean shouldShowHeroOnStart() {
        return this.config.getBoolean("powerOnStart.showHero", false);
    }

    public String getHeroGainedMessage() {
        return this.languageYAML.getString("Chat.gainedHero", "<bold><player> has gained the power of <hero>");
    }

    public String getNoPermissionMessage() {
        return this.languageYAML.getString("Chat.noPermission", "<dark_red>You do not have permission to execute this command!");
    }

    public String getCurrentHeroMessage() {
        return this.languageYAML.getString("Chat.currentHero", "<bold><player>, you are currently <hero>");
    }

    public Superhero getDefaultHero() {
        String name = this.config.getString("defaultHero.name", "Powerless");
        String colouredName = this.config.getString("defaultHero.colouredName", "<yellow><b>Powerless");
        String description = this.config.getString("defaultHero.description", "You have no power");
        Superhero hero = new Superhero(name, colouredName, description);
        ItemStack icon = new ItemStack(Material.BARRIER);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(legacySerializer.serialize(MiniMessage.miniMessage().deserialize(colouredName)));
        meta.setLore(List.of(legacySerializer.serialize(MiniMessage.miniMessage().deserialize("<white>" + description))));
        icon.setItemMeta(meta);
        return hero;
    }

    public String getHeroCooldownMessage() {
        return this.languageYAML.getString("Chat.heroCommandCooldown", "<bold><player>, /hero is currently on cooldown. You need to wait <currentcooldown>/<cooldown> more seconds!");
    }

    public String getInvalidHeroMessage() {
        return this.languageYAML.getString("Chat.invalidHeroMessage", "<bold><player>, You have entered an invalid hero name!");
    }

    public String getInvalidPlayerMessage() {
        return this.languageYAML.getString("Chat.invalidPlayerMessage", "<bold><player>, You have entered an invalid player name!");
    }

    public String getInvalidCommandMessage() {
        return this.languageYAML.getString("Chat.invalidCommandMessage", "<bold><player>, You have entered an invalid subcommand name!");
    }

    public String getInvalidRerollGroupMessage() {
        return this.languageYAML.getString("Chat.invalidRerollGroupMessage", "<bold><player>, You have entered an invalid reroll group name!");
    }

    public List<String> getCommandAliases() {
        return this.config.getStringList("heroCommand.aliases");
    }

    public long getHeroCommandCooldown() {
        return this.config.getLong("heroCommand.cooldown", 0L);
    }

    public String getDatabaseType() {
        return this.databaseYAML.getString("database.type", "LEGACY");
    }

    public String getDatabaseHost() {
        return this.databaseYAML.getString("database.host", "");
    }

    public String getDatabaseName() {
        return this.databaseYAML.getString("database.name", "");
    }

    public int getDatabasePort() {
        return this.databaseYAML.getInt("database.port", 3306);
    }

    public String getDatabaseUsername() {
        return this.databaseYAML.getString("database.username", "");
    }

    public String getDatabasePassword() {
        return this.databaseYAML.getString("database.password", "");
    }
}

