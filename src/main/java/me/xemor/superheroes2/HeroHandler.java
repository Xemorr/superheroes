package me.xemor.superheroes2;

import de.themoep.minedown.adventure.MineDown;
import me.xemor.superheroes2.events.HeroBlockBreakEvent;
import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class HeroHandler implements Listener {

    private HashMap<UUID, Superhero> uuidToPowers = new HashMap<>();
    private HashMap<String, Superhero> nameToSuperhero = new HashMap<>();
    Superheroes2 superheroes2;
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

    @EventHandler
    public void onBlockBroken(BlockBreakEvent e) {
        Collection<ItemStack> drops = e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer());
        HeroBlockBreakEvent heroBlockBreakEvent = new HeroBlockBreakEvent(e.getBlock(), e.getPlayer(), drops);
        heroBlockBreakEvent.callEvent();
    }

    public HeroHandler(Superheroes2 superheroes2, ConfigHandler configHandler) {
        this.configHandler = configHandler;
        this.superheroes2 = superheroes2;
        currentDataFile = new File(superheroes2.getDataFolder(), "data.yml");
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

    @NotNull
    public Superhero getSuperhero(Player player) {
        Superhero hero = uuidToPowers.get(player.getUniqueId());
        if (player.getGameMode() == GameMode.SPECTATOR && !hero.hasSkill(Skill.getSkill("PHASE"))) {
            return noPower;
        }
        if (hero == null) {
            return noPower;
        }
        return hero;
    }

    @NotNull
    public Superhero getSuperhero(UUID uuid) {
        Superhero hero = uuidToPowers.get(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            if (player.getGameMode() == GameMode.SPECTATOR && !hero.hasSkill(Skill.getSkill("PHASE"))) {
                return noPower;
            }
        }
        return hero;
    }

    /**
     * Executes setHeroInMemory with show as true.
     * @param player
     * @param hero
     */
    public void setHeroInMemory(Player player, Superhero hero) {
        setHeroInMemory(player, hero, true);
    }

    public void setHeroInMemory(Player player, Superhero hero, boolean show) {
        Superhero currentHero = uuidToPowers.get(player.getUniqueId());
        if (currentHero != null) {
            PlayerLostSuperheroEvent playerLostHeroEvent = new PlayerLostSuperheroEvent(player, currentHero);
            Bukkit.getServer().getPluginManager().callEvent(playerLostHeroEvent);
        }
        uuidToPowers.put(player.getUniqueId(), hero);
        if (show) {
            showHero(player, hero);
        }
        if (currentHero != hero) {
            PlayerGainedSuperheroEvent playerGainedPowerEvent = new PlayerGainedSuperheroEvent(player, hero);
            Bukkit.getServer().getPluginManager().callEvent(playerGainedPowerEvent);
        }
    }

    public void setHero(Player player, Superhero hero) {
        setHero(player, hero, true);
    }

    public void setHero(Player player, Superhero hero, boolean show) {
        setHeroInMemory(player, hero, show);
        saveSuperhero(player);
    }

    public Superhero loadPlayerHero(@NotNull Player player) {
        String heroString = currentDataYAML.getString(player.getUniqueId().toString());
        Superhero superhero = getSuperhero(heroString);
        if (superhero == null) {
            if (configHandler.isPowerOnStartEnabled()) {
                superhero = getRandomHero(player);
            }
            else {
                superhero = noPower;
            }
            if (configHandler.shouldShowHeroOnStart()) {
                showHero(player, superhero);
            }
        }
        uuidToPowers.put(player.getUniqueId(), superhero);
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
        Component colouredName = new MineDown(hero.getColouredName()).toComponent();
        Component description = new MineDown(hero.getDescription()).toComponent();
        Title title = Title.title(colouredName, description, Title.Times.of(Duration.ofMillis(500), Duration.ofMillis(5000), Duration.ofMillis(500)));
        Audience playerAudience = Superheroes2.getBukkitAudiences().player(player);
        playerAudience.showTitle(title);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5F, 1F);
        Component heroGainedMessage = new MineDown(configHandler.getHeroGainedMessage()).replace("hero", colouredName).replace("player", player.getDisplayName()).toComponent();
        playerAudience.sendMessage(heroGainedMessage);
    }

    @Nullable
    public Superhero getSuperhero(String name) {
        return nameToSuperhero.get(name);
    }

    public Superheroes2 getPlugin() {
        return superheroes2;
    }

    public HashMap<String, Superhero> getNameToSuperhero() {
        return nameToSuperhero;
    }
}
