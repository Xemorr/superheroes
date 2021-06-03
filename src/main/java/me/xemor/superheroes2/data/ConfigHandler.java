package me.xemor.superheroes2.data;

import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.configdata.ItemStackData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public class ConfigHandler {

    private File dataFolder;
    private File superpowersFolder;
    private File languageFile;
    private YamlConfiguration language;
    private FileConfiguration config;
    private Superheroes2 superheroes2;
    private ItemStack item;

    public ConfigHandler(Superheroes2 superheroes2) {
        this.superheroes2 = superheroes2;
        superheroes2.saveDefaultConfig();
        dataFolder = superheroes2.getDataFolder();
        config = superheroes2.getConfig();
        ItemStack itemStack = config.getItemStack("reroll.item");
        if (itemStack != null) {
            config.set("reroll.item.type", "DIAMOND_BLOCK");
            config.set("reroll.item.amount", 1);
            try {
                config.save(new File(dataFolder, "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConfigurationSection itemConfig = config.getConfigurationSection("reroll.item");
        ItemStackData itemStackData = new ItemStackData(itemConfig);
        item = itemStackData.getItem();
        superheroes2.saveResource("language.yml", false);
        languageFile = new File(dataFolder, "language.yml");
        language = YamlConfiguration.loadConfiguration(languageFile);
        handleSuperpowersFolder();
    }

    public void handleSuperpowersFolder() {
        superpowersFolder = new File(dataFolder, "powers");
        if (superpowersFolder.mkdir()) { //if the folder is generated, then add all the powers in.
            try {
                URI powers;
                try {
                    powers = this.getClass().getClassLoader().getResource("powers").toURI();
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
                    superheroes2.saveResource(path, false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<ConfigurationSection> getSuperheroesConfigurationSection() {
        List<ConfigurationSection> sections = new ArrayList<>();
        File[] files = superpowersFolder.listFiles();
        for (File file : files) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            Map<String, Object> values = yamlConfiguration.getValues(false);
            for (Object yamlObject : values.values()) {
                if (yamlObject instanceof ConfigurationSection) {
                    sections.add((ConfigurationSection) yamlObject);
                }
            }
        }
        return sections;
    }

    public void loadSuperheroes(HeroHandler heroHandler) {
        List<ConfigurationSection> sections = getSuperheroesConfigurationSection();
        HashMap<String, Superhero> nameToSuperhero = new HashMap<>();
        for (ConfigurationSection superheroSection : sections) {
            String superheroName = superheroSection.getName();
            String colouredSuperheroName = superheroSection.getString("colouredName", superheroName);
            String superheroDescription = superheroSection.getString("description", superheroName + " description");
            Superhero superhero = new Superhero(superheroName, colouredSuperheroName, superheroDescription);
            ConfigurationSection skillsSection = superheroSection.getConfigurationSection("skills");
            for (Object value : skillsSection.getValues(false).values()) {
                if (value instanceof ConfigurationSection) {
                    ConfigurationSection configurationSection = (ConfigurationSection) value;
                    String skillStr = configurationSection.getString("skill");
                    int skill;
                    try {
                        skill = Skill.getSkill(skillStr);
                    }
                    catch(IllegalArgumentException e) {
                        Bukkit.getLogger().log(Level.SEVERE, superheroName + " has encountered an invalid skill name!: " + skillStr);
                        e.printStackTrace();
                        continue;
                    }
                    superhero.addSkill(SkillData.create(skill, configurationSection));
                }
                else {
                    Bukkit.getLogger().log(Level.SEVERE, superheroName + " has encountered an invalid skill!");
                }
            }
            nameToSuperhero.put(superheroName, superhero);
        }
        heroHandler.registerHeroes(nameToSuperhero);
    }

    public void reloadConfig(HeroHandler heroHandler) {
        superheroes2.reloadConfig();
        config = superheroes2.getConfig();
        handleSuperpowersFolder();
        loadSuperheroes(heroHandler);
        heroHandler.setHeroesIntoMemory(new HashMap<>());
        language = YamlConfiguration.loadConfiguration(languageFile);
        ItemStackData itemStackData = new ItemStackData(config.getConfigurationSection("reroll.item"));
        item = itemStackData.getItem();
        for (Player player : Bukkit.getOnlinePlayers()) {
            heroHandler.loadPlayerHero(player);
        }
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public File getSuperpowersFolder() {
        return superpowersFolder;
    }

    public ItemStack getRerollItem() {
        return item;
    }

    public boolean isRerollEnabled() {
        return config.getConfigurationSection("reroll").getBoolean("isEnabled", true);
    }

    public boolean isPowerOnStartEnabled() {
        return config.getBoolean("powerOnStart.isEnabled", true);
    }

    public boolean shouldShowHeroOnStart() {
        return config.getBoolean("powerOnStart.showHero", true);
    }

    public String getHeroGainedMessage() {
        return language.getString("Chat.gainedHero", "&l%player% has gained the power of %hero%");
    }

    public String getNoPermissionMessage() {
        return language.getString("Chat.noPermission", "&4You do not have permission to execute this command!");
    }

    public String getCurrentHeroMessage() {
        return language.getString("Chat.currentHero", "&l%player%, you are currently %hero%");
    }

    public String getHeroCooldownMessage() {
        return language.getString("Chat.heroCommandCooldown", "&l%player%, /hero is currently on cooldown. You need to wait %currentcooldown%/%cooldown% more seconds!");
    }

    public double getHeroCommandCooldown() {
        return config.getDouble("heroCommand.cooldown", 0);
    }

    public double getRerollCooldown() { return config.getDouble("reroll.cooldown", 1.0);}

    public boolean areHeroPermissionsRequired() {return config.getConfigurationSection("reroll").getBoolean("eachHeroRequiresPermissions", false); }

}
