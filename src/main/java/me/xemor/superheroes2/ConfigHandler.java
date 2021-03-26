package me.xemor.superheroes2;

import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    private YamlConfiguration currentPowersYAML;
    private File currentPowersFile;
    private File dataFolder;
    private File superpowersFolder;
    private FileConfiguration config;
    private PowersHandler powersHandler;
    private Superheroes2 superheroes2;

    public ConfigHandler(Superheroes2 superheroes2, PowersHandler powersHandler) {
        this.superheroes2 = superheroes2;
        superheroes2.saveDefaultConfig();
        config = superheroes2.getConfig();
        this.powersHandler = powersHandler;
        dataFolder = superheroes2.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        currentPowersFile = new File(dataFolder, "data.yml");
        if (!currentPowersFile.exists()) {
            try {
                currentPowersFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        currentPowersYAML = YamlConfiguration.loadConfiguration(currentPowersFile);
        handleSuperpowersFolder();
        loadSuperheroes();
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

    public void loadSuperheroes() {
        List<ConfigurationSection> sections = getSuperheroesConfigurationSection();
        HashMap<String, Superhero> nameToSuperhero = new HashMap<>();
        for (ConfigurationSection superheroSection : sections) {
            String superheroName = superheroSection.getName();
            String colouredSuperheroName = ChatColor.translateAlternateColorCodes('&', superheroSection.getString("colouredName", superheroName));
            String superheroDescription = ChatColor.translateAlternateColorCodes('&', superheroSection.getString("description", superheroName + " description"));
            String superheroHeroInfo = ChatColor.translateAlternateColorCodes('&', superheroSection.getString("heroInfo", superheroName));
            Superhero superhero = new Superhero(superheroName, colouredSuperheroName, superheroDescription, superheroHeroInfo);
            ConfigurationSection skillsSection = superheroSection.getConfigurationSection("skills");
            for (Map.Entry<String, Object> keyValuePair : skillsSection.getValues(false).entrySet()) {
                if (keyValuePair.getValue() instanceof ConfigurationSection) {
                    ConfigurationSection configurationSection = (ConfigurationSection) keyValuePair.getValue();
                    String skillStr = configurationSection.getString("skill");
                    Skill skill = null;
                    try {
                        skill = Skill.valueOf(skillStr);
                    }
                    catch(IllegalArgumentException e) {
                        Bukkit.getLogger().log(Level.SEVERE, superheroName + " has encountered an invalid skill name!");
                        e.printStackTrace();
                    }
                    superhero.addSkill(SkillData.create(skill, configurationSection));
                }
                else {
                    Bukkit.getLogger().log(Level.SEVERE, superheroName + " has encountered an invalid skill!");
                }
            }
            nameToSuperhero.put(superheroName, superhero);
        }
        powersHandler.registerHeroes(nameToSuperhero);
    }

    public void reloadConfig() {
        saveCurrentPowers();
        powersHandler.getPlugin().reloadConfig();
        config = powersHandler.getPlugin().getConfig();
        handleSuperpowersFolder();
        loadSuperheroes();
        powersHandler.setHeroes(new HashMap<>());
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayerHero(player);
        }
    }

    public Superhero loadPlayerHero(Player player) {
        String heroString = currentPowersYAML.getString(player.getUniqueId().toString());
        Superhero superhero = powersHandler.getSuperhero(heroString);
        if (superhero == null) {
            if (isRerollEnabled()) {
                superhero = powersHandler.getRandomHero(player);
            }
            else {
                superhero = powersHandler.noPower;
            }
            powersHandler.setHero(player, superhero);
        }
        else {
            powersHandler.getUuidToPowers().put(player.getUniqueId(), superhero);
        }
        return superhero;
    }

    public void saveSuperhero(Player player, Superhero hero) {
        saveSuperhero(player.getUniqueId(), hero);
    }

    public void saveSuperhero(UUID uuid, Superhero hero) {
        currentPowersYAML.set(String.valueOf(uuid), hero.getName());
        saveCurrentPowers();
    }

    public void saveCurrentPowers() {
        try {
            currentPowersYAML.save(currentPowersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getCurrentPowersYAML() {
        return currentPowersYAML;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public File getSuperpowersFolder() {
        return superpowersFolder;
    }

    public ItemStack getRerollItem() {
        return config.getConfigurationSection("reroll").getItemStack("item");
    }

    public boolean isRerollEnabled() {
        return config.getConfigurationSection("reroll").getBoolean("isEnabled", true);
    }

    public boolean isInfoEnabled() {
        return config.getConfigurationSection("info").getBoolean("isEnabled", true);
    }

    public double getRerollCooldown() { return config.getConfigurationSection("reroll").getDouble("cooldown", 1.0);}

    public boolean areHeroPermissionsRequired() {return config.getConfigurationSection("reroll").getBoolean("eachHeroRequiresPermissions", false); }

}
