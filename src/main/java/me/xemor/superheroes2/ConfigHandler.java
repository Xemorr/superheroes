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

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class ConfigHandler {

    private YamlConfiguration currentPowersYAML;
    private File currentPowersFile;
    private File dataFolder;
    private File superpowersFolder;
    private FileConfiguration config;
    private PowersHandler powersHandler;
    private final static String[] resources = new String[]{"enderman", "scavenger", "strongman", "doomfist", "eraserhead", "phase", "pickpocket", "superhuman", "mole", "robot", "slime", "aerosurfer", "trap", "chicken", "frozone", "lavawalker"};


    public ConfigHandler(Superheroes2 superheroes2, PowersHandler powersHandler) {
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
        superpowersFolder = new File(dataFolder + File.separator + "powers" + File.separator);
        if (!superpowersFolder.exists()) {
            superpowersFolder.mkdir();
            for (String resource : resources) {
                superheroes2.saveResource("powers" + File.separator + resource + ".yml", false);
            }
        }
        loadSuperheroes();
        loadPlayerHeroes();
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
            String colouredSuperheroName = ChatColor.translateAlternateColorCodes('&', superheroSection.getString("colouredName"));
            String superheroDescription = ChatColor.translateAlternateColorCodes('&', superheroSection.getString("description"));
            Superhero superhero = new Superhero(superheroName, colouredSuperheroName, superheroDescription);
            ConfigurationSection skillsSection = superheroSection.getConfigurationSection("skills");
            for (Map.Entry<String, Object> keyValuePair : skillsSection.getValues(false).entrySet()) {
                if (keyValuePair.getValue() instanceof ConfigurationSection) {
                    ConfigurationSection configurationSection = (ConfigurationSection) keyValuePair.getValue();
                    String skillStr = configurationSection.getString("skill");
                    Skill skill = null;
                    try {
                        skill = Skill.valueOf(skillStr);
                    }catch(IllegalArgumentException e) {
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

    public void loadPlayerHeroes() {
        HashMap<UUID, Superhero> playerHeroes = new HashMap<>();
        for (Map.Entry<String, Object> entry :  currentPowersYAML.getValues(false).entrySet()) {
            Superhero superhero = powersHandler.getSuperhero((String) entry.getValue());
            UUID uuid = UUID.fromString(entry.getKey());
            if (superhero == null) {
                Superhero randomHero = powersHandler.getRandomHero();
                saveSuperhero(uuid, randomHero);
                playerHeroes.put(uuid, randomHero);
            }
            else {
                playerHeroes.put(uuid, superhero);
            }
        }
        powersHandler.setHeroes(playerHeroes);
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
        return config.getConfigurationSection("reroll").getBoolean("isEnabled");
    }

}
