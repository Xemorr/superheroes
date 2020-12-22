package me.xemor.superheroes2;

import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class PowersHandler {

    HashMap<UUID, Superhero> uuidToPowers = new HashMap<>();
    FileConfiguration powersFile;
    Superheroes2 superheroes;
    ConfigHandler configHandler;
    Superhero erased = new Superhero("ERASED", ChatColor.translateAlternateColorCodes('&', "&7&lERASED"), "Their power has been erased");
    Superhero noPower = new Superhero("NOPOWER", ChatColor.translateAlternateColorCodes('&', "&e&lNOPOWER"), "They have no power");
    private HashMap<String, Superhero> nameToSuperhero = new HashMap<>();


    public PowersHandler(Superheroes2 superheroes) {
        this.superheroes = superheroes;
    }

    public void setConfigHandler(ConfigHandler configHandler) {
        this.configHandler = configHandler;
        powersFile = configHandler.getCurrentPowersYAML();
    }

    public void registerHeroes(HashMap<String, Superhero> nameToSuperhero) {
        this.nameToSuperhero = nameToSuperhero;
        nameToSuperhero.put("NOPOWER", noPower);
    }

    public void setHeroes(HashMap<UUID, Superhero> playerHeroes) {
        this.uuidToPowers = playerHeroes;
    }

    public Superhero getSuperhero(Player player) {
        Superhero hero = uuidToPowers.get(player.getUniqueId());
        if (player.getGameMode() == GameMode.SPECTATOR && !hero.hasSkill(Skill.PHASE)) {
            return noPower;
        }
        return hero;
    }

    public void setHero(Player player, Superhero hero) {
        Superhero currentHero = uuidToPowers.get(player.getUniqueId());
        if (currentHero != null) {
            PlayerLostSuperheroEvent playerLostHeroEvent = new PlayerLostSuperheroEvent(player, currentHero);
            Bukkit.getServer().getPluginManager().callEvent(playerLostHeroEvent);
        }
        uuidToPowers.put(player.getUniqueId(), hero);
        showPower(player, hero);
        configHandler.saveSuperhero(player, hero);
        if (currentHero != hero) {
            PlayerGainedSuperheroEvent playerGainedPowerEvent = new PlayerGainedSuperheroEvent(player, hero);
            Bukkit.getServer().getPluginManager().callEvent(playerGainedPowerEvent);
        }
    }

    public void temporarilyRemovePower(Player player, Player remover, int timeInTicks) {
        final Superhero oldPower = uuidToPowers.get(player.getUniqueId());
        uuidToPowers.replace(player.getUniqueId(), erased);
        if (remover != null) {
            remover.sendMessage(ChatColor.BOLD + player.getName() + " has had their power erased temporarily!");
        }
        player.sendMessage(ChatColor.BOLD + player.getName() + " has had their power erased temporarily!");
        PlayerLostSuperheroEvent playerLostPowerEvent = new PlayerLostSuperheroEvent(player, oldPower);
        Bukkit.getServer().getPluginManager().callEvent(playerLostPowerEvent);
        showPower(player, erased);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (uuidToPowers.get(player.getUniqueId()) == erased) {
                    PlayerGainedSuperheroEvent playerGainedPowerEvent = new PlayerGainedSuperheroEvent(player, oldPower);
                    Bukkit.getServer().getPluginManager().callEvent(playerGainedPowerEvent);
                    uuidToPowers.put(player.getUniqueId(), oldPower);
                    Bukkit.broadcastMessage(ChatColor.BOLD + player.getName() + " has had their powers reinstated!");
                    showPower(player, oldPower);
                }
            }
        }.runTaskLater(superheroes, timeInTicks);
    }

    public void setRandomHero(Player player) {
        Superhero newHero = getRandomHero();
        this.setHero(player, newHero);
    }

    public Superhero getRandomHero() {
        Object[] superheroes = nameToSuperhero.values().toArray();
        Superhero superhero = noPower;
        while (superhero == noPower) {
            Random random = new Random();
            int rng = random.nextInt(superheroes.length);
            superhero = (Superhero) superheroes[rng];
        }
        return superhero;
    }

    public void showPower(Player player, Superhero hero) {
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

    public Superhero getNoPower() {
        return noPower;
    }
}
