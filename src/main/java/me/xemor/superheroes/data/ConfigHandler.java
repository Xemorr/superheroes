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
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
import java.util.logging.Level;
import java.util.stream.Stream;

public class ConfigHandler {

    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build();

    private File dataFolder;
    private File superpowersFolder;
    private File languageFile;
    private YamlConfiguration language;
    private FileConfiguration config;
    private Superheroes superheroes;
    private ItemComparisonData item;

    public ConfigHandler(Superheroes superheroes) {
        this.superheroes = superheroes;
        superheroes.saveDefaultConfig();
        dataFolder = superheroes.getDataFolder();
        superpowersFolder = new File(dataFolder, "powers");
        config = superheroes.getConfig();
        item = new ItemComparisonData(Objects.requireNonNull(config.getConfigurationSection("reroll.item")));
        if (!new File(superheroes.getDataFolder(), "language.yml").exists()) superheroes.saveResource("language.yml", false);
        languageFile = new File(dataFolder, "language.yml");
        language = YamlConfiguration.loadConfiguration(languageFile);
        handleSuperpowersFolder();
    }

    public void handleSuperpowersFolder() {
        if (superpowersFolder.mkdir()) { //if the folder is generated (and needed to be generated), then add all the powers in.
            try {
                URI powers;
                try {
                    powers = getClass().getClassLoader().getResource("powers").toURI();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    return;
                }
                Path myPath;
                if (powers.getScheme().equals("jar")) {
                    FileSystem fileSystem = FileSystems.newFileSystem(powers, Collections.emptyMap());
                    myPath = fileSystem.getPath("powers");
                } else {
                    myPath = Paths.get(powers);
                }
                Stream<Path> walk = Files.walk(myPath, 1);
                Iterator<Path> it = walk.iterator();
                it.next();
                while (it.hasNext()){
                    String path = it.next().toString();
                    if (path.charAt(0) == '/') {
                        path = path.substring(1);
                    }
                    superheroes.saveResource(path, false);
                }
                walk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<ConfigurationSection> getSuperheroesConfigurationSection() {
        List<ConfigurationSection> sections = new ArrayList<>();
        File[] files = superpowersFolder.listFiles();
        for (File file : files) {
            if (file.getName().endsWith("yml")) {
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                Map<String, Object> values = yamlConfiguration.getValues(false);
                for (Object yamlObject : values.values()) {
                    if (yamlObject instanceof ConfigurationSection) {
                        sections.add((ConfigurationSection) yamlObject);
                    }
                }
            }
        }
        return sections;
    }

    public void loadSkills(Superhero superhero, ConfigurationSection superheroSection) {
        ConfigurationSection skillsSection = superheroSection.getConfigurationSection("skills");
        if (skillsSection == null) Superheroes.getInstance().getLogger().severe("The skills section is missing/invalid at " + superheroSection.getCurrentPath() + ".skills");
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
            }
            else {
                Bukkit.getLogger().log(Level.SEVERE, superhero.getName() + " has encountered an invalid skill!");
            }
        }
    }

    public void loadSuperheroes(HeroHandler heroHandler) {
        List<ConfigurationSection> sections = getSuperheroesConfigurationSection();
        HashMap<String, Superhero> nameToSuperhero = new HashMap<>();
        for (ConfigurationSection superheroSection : sections) {
            try { // prevent one hero from breaking another
                String superheroName = superheroSection.getName();
                String colouredSuperheroName = superheroSection.getString("colouredName", superheroName);
                String superheroDescription = superheroSection.getString("description", superheroName + " description");
                String base64Skin = superheroSection.getString("skin.value");
                String signature = superheroSection.getString("skin.signature");
                Superhero superhero = new Superhero(superheroName, colouredSuperheroName, superheroDescription);
                superhero.setBase64Skin(base64Skin);
                superhero.setSignature(signature);
                calculateIcon(superhero, superheroSection);
                loadSkills(superhero, superheroSection);
                SuperheroLoadEvent superheroLoadEvent = new SuperheroLoadEvent(superhero, superheroSection);
                Bukkit.getServer().getPluginManager().callEvent(superheroLoadEvent);
                if (!superheroLoadEvent.isCancelled()) nameToSuperhero.put(superheroName.toLowerCase(), superhero);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        heroHandler.registerHeroes(nameToSuperhero);
    }

    // Used in loadSuperheroes for working out the icon for the GUI
    private void calculateIcon(Superhero hero, ConfigurationSection heroSection) {
        ItemStack icon;
        ConfigurationSection section = heroSection.getConfigurationSection("icon");
        Component colouredName = MiniMessage.miniMessage().deserialize(hero.getColouredName());
        if (section != null) {
            icon = new ItemStackData(heroSection.getConfigurationSection("icon")).getItem();
        }
        else {
            if (hero.getBase64Skin().equals("")) { // approximate wool colour based on coloured name
                TextColor color = colouredName.color();
                if (color == null) icon = new ItemStack(Material.BLACK_WOOL);
                else icon = new ItemStack(woolFromColor(color.red(), color.green(), color.blue()));
            }
            else {
                icon = SkullCreator.itemFromBase64(hero.getBase64Skin());
            }
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
        org.bukkit.DyeColor closest = DyeColor.BLACK;
        for (org.bukkit.DyeColor dye : org.bukkit.DyeColor.values()) {
            org.bukkit.Color color = dye.getColor();
            int dist = Math.abs(color.getRed() - red) + Math.abs(color.getGreen() - green) + Math.abs(color.getBlue() - blue);
            if (dist < distance) {
                distance = dist;
                closest = dye;
            }
        }
        // You might want to add a try here - I'm not sure how it worked back in 1.14 (it may produce a NullPointerException)
        return Material.getMaterial((closest.name() + "_WOOL").toUpperCase());
    }

    public void reloadConfig(HeroHandler heroHandler) {
        SuperheroesReloadEvent superheroesReloadEvent = new SuperheroesReloadEvent();
        Bukkit.getServer().getPluginManager().callEvent(superheroesReloadEvent);
        superheroes.reloadConfig();
        config = superheroes.getConfig();
        heroHandler.loadConfigItems();
        handleSuperpowersFolder();
        heroHandler.handlePlayerData();
        loadSuperheroes(heroHandler);
        language = YamlConfiguration.loadConfiguration(languageFile);
        item = new ItemComparisonData(Objects.requireNonNull(config.getConfigurationSection("reroll.item")));
        heroHandler.setHeroesIntoMemory(new HashMap<>());
        for (Player player : Bukkit.getOnlinePlayers()) {
            heroHandler.loadSuperheroPlayer(player);
        }
    }

    public void saveConfig() {
        Bukkit.getScheduler().runTaskAsynchronously(superheroes, () -> superheroes.saveConfig());
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public File getSuperpowersFolder() {
        return superpowersFolder;
    }

    public ItemComparisonData getRerollItem() {
        return item;
    }

    public boolean isRerollEnabled() {
        return config.getBoolean("reroll.isEnabled", true);
    }

    public boolean isPowerOnStartEnabled() {
        return config.getBoolean("powerOnStart.isEnabled", false);
    }

    public boolean openGUIOnStart() {
        return config.getBoolean("gui.onStart", true);
    }

    public String getGUIName() {
        return language.getString("GUI.name", "Pick your hero!");
    }

    public boolean canCloseGUI() {
        return config.getBoolean("gui.canClose", false);
    }

    public List<String> getDisabledWorlds() {
        return config.getStringList("disabledWorlds");
    }

    public boolean shouldShowHeroOnStart() {
        return config.getBoolean("powerOnStart.showHero", false);
    }

    public String getHeroGainedMessage() {
        return language.getString("Chat.gainedHero", "<bold><player> has gained the power of <hero>");
    }

    public String getNoPermissionMessage() {
        return language.getString("Chat.noPermission", "<dark_red>You do not have permission to execute this command!");
    }

    public String getCurrentHeroMessage() {
        return language.getString("Chat.currentHero", "<bold><player>, you are currently <hero>");
    }

    public Superhero getDefaultHero() {
        String name = config.getString("defaultHero.name", "Powerless");
        String colouredName = config.getString("defaultHero.colouredName", "<yellow><b>Powerless");
        String description = config.getString("defaultHero.description", "You have no power");
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
        return language.getString("Chat.heroCommandCooldown", "<bold><player>, /hero is currently on cooldown. You need to wait <currentcooldown>/<cooldown> more seconds!");
    }

    public String getInvalidHeroMessage() {
        return language.getString("Chat.invalidHeroMessage", "<bold><player>, You have entered an invalid hero name!");
    }

    public String getInvalidPlayerMessage() {
        return language.getString("Chat.invalidPlayerMessage", "<bold><player>, You have entered an invalid player name!");
    }

    public String getInvalidCommandMessage() {
        return language.getString("Chat.invalidCommandMessage", "<bold><player>, You have entered an invalid subcommand name!");
    }

    public List<String> getCommandAliases() {
        return config.getStringList("heroCommand.aliases");
    }

    public long getHeroCommandCooldown() {
        return config.getLong("heroCommand.cooldown", 0);
    }

    public double getRerollCooldown() {
        return config.getDouble("reroll.cooldown", 1.0);
    }

    public boolean areHeroPermissionsRequired() {return config.getConfigurationSection("reroll").getBoolean("eachHeroRequiresPermissions", false); }

    public String getDatabaseType() {
        return config.getString("database.type", "LEGACY");
    }

    public void setDatabaseType(String newType) {
        ConfigurationSection databaseSection = getDatabaseSection();
        databaseSection.set("type", newType);
        config.set("database", databaseSection);
        saveConfig();
    }

    public void setTextConvertComplete() {
        config.set("textconvert",true);
    }

    public boolean getTextConvertStatus(){
        return config.getBoolean("textconvert",false);
    }

    public String getDatabaseHost() {
        return config.getString("database.host", "");
    }

    public String getDatabaseName() {
        return config.getString("database.name", "");
    }

    public int getDatabasePort() {
        return config.getInt("database.port", 3306);
    }

    public String getDatabaseUsername() {
        return config.getString("database.username", "");
    }

    public String getDatabasePassword() {
        return config.getString("database.password", "");
    }

    private ConfigurationSection getDatabaseSection() {
        return config.getConfigurationSection("database");
    }

}
