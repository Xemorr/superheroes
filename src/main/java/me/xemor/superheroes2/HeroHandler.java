package me.xemor.superheroes2;

import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HeroHandler implements Listener {

    private HashMap<UUID, Superhero> uuidToPowers = new HashMap<>();
    private HashMap<String, Superhero> nameToSuperhero = new HashMap<>();
    Superheroes2 superheroes;
    private YamlConfiguration currentDataYAML;
    private File currentDataFile;
    private final ConfigHandler configHandler;
    Superhero noPower = new Superhero("NOPOWER", ChatColor.translateAlternateColorCodes('&', "&e&lNOPOWER"), "They have no power");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        loadPlayerHero(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        uuidToPowers.remove(e.getPlayer().getUniqueId());
    }

    public HeroHandler(Superheroes2 superheroes, ConfigHandler configHandler) {
        this.configHandler = configHandler;
        this.superheroes = superheroes;
        currentDataFile = new File(superheroes.getDataFolder(), "data.yml");
        try {
            currentDataFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentDataYAML = YamlConfiguration.loadConfiguration(currentDataFile);
    }

    public void registerHeroes(HashMap<String, Superhero> nameToSuperhero) {
        this.nameToSuperhero = nameToSuperhero;
        nameToSuperhero.put("NOPOWER", noPower);
    }

    public void setHeroesIntoMemory(HashMap<UUID, Superhero> playerHeroes) {
        this.uuidToPowers = playerHeroes;
    }

    public Superhero getSuperhero(Player player) {
        Superhero hero = uuidToPowers.get(player.getUniqueId());
        if (player.getGameMode() == GameMode.SPECTATOR && !hero.hasSkill(Skill.PHASE)) {
            return noPower;
        }
        return hero;
    }

    public Superhero getSuperhero(UUID uuid) {
        Superhero hero = uuidToPowers.get(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            if (player.getGameMode() == GameMode.SPECTATOR && !hero.hasSkill(Skill.PHASE)) {
                return noPower;
            }
        }
        return hero;
    }

    public void setHeroInMemory(Player player, Superhero hero) {
        Superhero currentHero = uuidToPowers.get(player.getUniqueId());
        if (currentHero != null) {
            PlayerLostSuperheroEvent playerLostHeroEvent = new PlayerLostSuperheroEvent(player, currentHero);
            Bukkit.getServer().getPluginManager().callEvent(playerLostHeroEvent);
        }
        uuidToPowers.put(player.getUniqueId(), hero);
        showHero(player, hero);
        if (currentHero != hero) {
            PlayerGainedSuperheroEvent playerGainedPowerEvent = new PlayerGainedSuperheroEvent(player, hero);
            Bukkit.getServer().getPluginManager().callEvent(playerGainedPowerEvent);
        }
    }

    public void setHero(Player player, Superhero hero) {
        setHeroInMemory(player, hero);
        saveSuperhero(player);
    }

    public Superhero loadPlayerHero(Player player) {
        String heroString = currentDataYAML.getString(player.getUniqueId().toString());
        Superhero superhero = getSuperhero(heroString);
        if (superhero == null) {
            if (configHandler.isRerollEnabled()) {
                superhero = getRandomHero(player);
            }
            else {
                superhero = noPower;
            }
            setHero(player, superhero);
        }
        else {
            uuidToPowers.put(player.getUniqueId(), superhero);
        }
        return superhero;
    }

    public void saveSuperhero(Player player) {
        currentDataYAML.set(String.valueOf(player.getUniqueId()), getSuperhero(player).getName());
        try {
            currentDataYAML.save(currentDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSuperhero(UUID uuid) {
        currentDataYAML.set(String.valueOf(uuid), getSuperhero(uuid).getName());
        try {
            currentDataYAML.save(currentDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRandomHero(Player player) {
        Superhero superhero = getRandomHero(player);
        setHero(player, superhero);
    }

    public Superhero getRandomHero(Player player) {
        List<Superhero> superheroes = new ArrayList<>(nameToSuperhero.values());
        Collections.shuffle(superheroes);
        Superhero newHero = noPower;
        for (Superhero superhero : superheroes) {
            if (configHandler.areHeroPermissionsRequired() && !player.hasPermission(superhero.getPermission())) {
                continue;
            }
            newHero = superhero;
            break;
        }
        return newHero;
    }

    public void showHero(Player player, Superhero hero) {
        player.sendTitle(hero.getColouredName(), hero.getDescription(), 10, 100, 10);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5F, 1F);
        Bukkit.broadcastMessage(ChatColor.BOLD + player.getName() + " has gained the power of " + hero.getColouredName());
    }

    public Superhero getSuperhero(String name) {
        return nameToSuperhero.get(name);
    }

    public Superheroes2 getPlugin() {
        return superheroes;
    }

    public HashMap<String, Superhero> getNameToSuperhero() {
        return nameToSuperhero;
    }
}
