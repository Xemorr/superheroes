package me.xemor.superheroes.data;

import dev.dbassett.skullcreator.SkullCreator;
import me.xemor.configurationdata.ItemStackData;
import me.xemor.configurationdata.comparison.ItemComparisonData;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.events.SuperheroLoadEvent;
import me.xemor.superheroes.events.SuperheroesReloadEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
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

    public List<ConfigurationSection> getSuperheroesConfigurationSection() {
        File powersFolder = this.getPowersFolder();
        File[] files = powersFolder.listFiles();
        if (files == null) return Collections.emptyList();
        return (Arrays.stream(files).parallel()).map(file -> {
            if (file.getName().endsWith("yml")) {
                YamlConfiguration yamlConfiguration;
                try {
                    yamlConfiguration = new YamlConfiguration();
                    yamlConfiguration.load(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InvalidConfigurationException e) {
                    Superheroes.getInstance().getLogger().severe(file.getName() + " is an invalid YAML file!");
                    Superheroes.getInstance().getLogger().severe(e.getMessage());
                    Superheroes.getInstance().getLogger().severe("Please try using: https://codebeautify.org/yaml-parser-online to fix this issue!");
                    return null;
                }
                return yamlConfiguration.getValues(false).values();
            }
            return null;
        }).filter(Objects::nonNull).flatMap(Collection::stream).filter(o -> o instanceof ConfigurationSection).map(o -> (ConfigurationSection) o).toList();
    }

    private File getPowersFolder() {
        return new File(this.getDataFolder(), "powers");
    }

    public void loadSkills(Superhero superhero, ConfigurationSection superheroSection) {
        ConfigurationSection skillsSection = superheroSection.getConfigurationSection("skills");
        if (skillsSection == null) {
            Superheroes.getInstance().getLogger().severe("The skills section is missing/invalid at " + superheroSection.getCurrentPath() + ".skills");
        }
        for (Object value : skillsSection.getValues(false).values()) {
            if (value instanceof ConfigurationSection configurationSection) {
                String skillStr = configurationSection.getString("skill");
                int skill = Skill.getSkill(skillStr);
                if (skill == -1) {
                    Bukkit.getLogger().log(Level.SEVERE, superhero.getName() + " has encountered an invalid skill name!: " + configurationSection.getCurrentPath() + ".skill" + skillStr);
                    continue;
                }
                SkillData skillData = SkillData.create(skill, configurationSection);
                try {
                    superhero.addSkill(skillData);
                } catch (NullPointerException e) {
                    Superheroes.getInstance().getLogger().severe("SkillData is null! This skill has not been registered!" + configurationSection.getCurrentPath());
                }
                continue;
            }
            Bukkit.getLogger().log(Level.SEVERE, superhero.getName() + " has encountered an invalid skill!");
        }
    }

    public void loadSuperheroes(HeroHandler heroHandler) {
        List<ConfigurationSection> sections = this.getSuperheroesConfigurationSection();
        HashMap<String, Superhero> nameToSuperhero = new HashMap<>();
        for (ConfigurationSection superheroSection : sections) {
            try {
                String superheroName = superheroSection.getName();
                String colouredSuperheroName = superheroSection.getString("colouredName", superheroName);
                String superheroDescription = superheroSection.getString("description", superheroName + " description");
                String base64Skin = superheroSection.getString("skin.value");
                String signature = superheroSection.getString("skin.signature");
                Superhero superhero = new Superhero(superheroName, colouredSuperheroName, superheroDescription);
                superhero.setBase64Skin(base64Skin);
                superhero.setSignature(signature);
                this.calculateIcon(superhero, superheroSection);
                this.loadSkills(superhero, superheroSection);
                SuperheroLoadEvent superheroLoadEvent = new SuperheroLoadEvent(superhero, superheroSection);
                Bukkit.getServer().getPluginManager().callEvent(superheroLoadEvent);
                if (superheroLoadEvent.isCancelled()) continue;
                nameToSuperhero.put(superheroName.toLowerCase(), superhero);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        heroHandler.registerHeroes(nameToSuperhero);
    }

    private void calculateIcon(Superhero hero, ConfigurationSection heroSection) {
        ItemStack icon;
        ConfigurationSection section = heroSection.getConfigurationSection("icon");
        Component colouredName = MiniMessage.miniMessage().deserialize(hero.getColouredName());
        if (section != null) {
            icon = new ItemStackData(heroSection.getConfigurationSection("icon")).getItem();
        } else {
            TextColor color;
            icon = hero.getBase64Skin().equals("") ? ((color = colouredName.color()) == null ? new ItemStack(Material.BLACK_WOOL) : new ItemStack(this.woolFromColor(color.red(), color.green(), color.blue()))) : SkullCreator.itemFromBase64(hero.getBase64Skin());
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(legacySerializer.serialize(colouredName));
            Component description = MiniMessage.miniMessage().deserialize(hero.getDescription());
            description = description.colorIfAbsent(TextColor.color(255, 255, 255));
            meta.setLore(List.of(legacySerializer.serialize(description)));
            icon.setItemMeta(meta);
        }
        hero.setIcon(icon);
    }

    public Material woolFromColor(int red, int green, int blue) {
        int distance = Integer.MAX_VALUE;
        DyeColor closest = DyeColor.BLACK;
        for (DyeColor dye : DyeColor.values()) {
            Color color = dye.getColor();
            int dist = Math.abs(color.getRed() - red) + Math.abs(color.getGreen() - green) + Math.abs(color.getBlue() - blue);
            if (dist >= distance) continue;
            distance = dist;
            closest = dye;
        }
        return Material.getMaterial((closest.name() + "_WOOL").toUpperCase());
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
        hero.setIcon(icon);
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

