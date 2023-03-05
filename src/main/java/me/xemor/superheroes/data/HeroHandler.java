package me.xemor.superheroes.data;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes.events.SuperheroPlayerJoinEvent;
import me.xemor.superheroes.skills.Skill;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class HeroHandler {

    private final HashMap<UUID, SuperheroPlayer> uuidToData = new HashMap<>();
    private HashMap<String, Superhero> nameToSuperhero = new HashMap<>();
    private final Superheroes superheroes;
    private final ConfigHandler configHandler;
    private Superhero noPower;
    private HeroIOHandler heroIOHandler;

    private List<String> disabledWorlds;

    private final ConcurrentHashMap<UUID, CompletableFuture<SuperheroPlayer>> isProcessing = new ConcurrentHashMap<>();

    public HeroHandler(Superheroes superheroes, ConfigHandler configHandler) {
        this.configHandler = configHandler;
        this.superheroes = superheroes;
        loadConfigItems();
    }

    /*
        Calls ConfigHandler methods to fetch default hero and disabledWorlds
     */
    public void loadConfigItems() {
        noPower = configHandler.getDefaultHero();
        disabledWorlds = configHandler.getDisabledWorlds();
    }

    public void registerHeroes(HashMap<String, Superhero> nameToSuperhero) {
        this.nameToSuperhero = nameToSuperhero;
        nameToSuperhero.put(noPower.getName().toLowerCase(), noPower);
    }

    public void setHeroesIntoMemory(HashMap<UUID, SuperheroPlayer> playerHeroes) {
        this.uuidToData.clear();
        this.uuidToData.putAll(playerHeroes);
    }

    public void handlePlayerData() {
        heroIOHandler = new HeroIOHandler();
        heroIOHandler.handlePlayerData();
    }

    public SuperheroPlayer getSuperheroPlayer(Player player) {
        return uuidToData.get(player.getUniqueId());
    }

    @NotNull
    public Superhero getSuperhero(Player player) {
        if (disabledWorlds.contains(player.getWorld().getName())) {
            return noPower;
        }
        SuperheroPlayer heroPlayer = uuidToData.get(player.getUniqueId());
        if (heroPlayer == null) {
            return noPower;
        }
        Superhero hero = heroPlayer.getSuperhero();
        if (player.getGameMode() == GameMode.SPECTATOR && !hero.hasSkill(Skill.getSkill("PHASE"))) {
            return noPower;
        }
        return hero;
    }

    @NotNull
    public Superhero getSuperhero(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        return getSuperhero(player);
    }

    /**
     * Executes setHeroInMemory with show as true.
     *
     * @param player
     * @param hero
     */
    public void setHeroInMemory(Player player, Superhero hero) {
        setHeroInMemory(player, hero, true);
    }

    public void setHeroInMemory(Player player, Superhero hero, boolean show) {
        SuperheroPlayer superheroPlayer = uuidToData.get(player.getUniqueId());
        if (superheroPlayer == null) {
            superheroPlayer = new SuperheroPlayer(player.getUniqueId(), noPower, 0);
            uuidToData.put(player.getUniqueId(), superheroPlayer);
        }
        Superhero currentHero = superheroPlayer.getSuperhero();
        superheroPlayer.setSuperhero(hero);
        PlayerLostSuperheroEvent playerLostHeroEvent = new PlayerLostSuperheroEvent(player, currentHero);
        Bukkit.getServer().getPluginManager().callEvent(playerLostHeroEvent);
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
        heroIOHandler.saveSuperheroPlayerAsync(getSuperheroPlayer(player));
    }

    public void preLoginLoadSuperheroPlayer(@NotNull UUID uuid) {
        CompletableFuture<SuperheroPlayer> future = heroIOHandler.loadSuperHeroPlayerAsync(uuid);
        isProcessing.put(uuid, future);
        future.thenAccept((superheroPlayer) -> Bukkit.getScheduler().runTask(superheroes, () -> {
            if (superheroPlayer != null) {
                uuidToData.putIfAbsent(uuid, superheroPlayer);
            }
            isProcessing.remove(uuid); // processed doesn't need to be locked / synchronised as guaranteed to be running in a Bukkit Thread
        }));
    }

    public void finalLoadSuperheroPlayer(@NotNull Player player) {
        SuperheroPlayer superheroPlayer = uuidToData.get(player.getUniqueId());
        if (isProcessing.containsKey(player.getUniqueId()))  {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Level severity = Level.INFO;
                    if (isProcessing.size() > 60) severity = Level.WARNING;
                    superheroes.getLogger().log(severity, "The player " + player + " has took a while to load its data. Waiting before processing further.");
                    finalLoadSuperheroPlayer(player); // really, the player should have processed before reaching PlayerJoinEvent (or at least that's my expectation)
                }
            }.runTaskLater(superheroes, 2L);
        }
        else {
            if (superheroPlayer == null) {
                Superhero superhero;
                if (configHandler.isPowerOnStartEnabled()) {
                    superhero = getRandomHero(player);
                } else {
                    superhero = noPower;
                }
                setHero(player, superhero, configHandler.shouldShowHeroOnStart());
            }
            SuperheroPlayerJoinEvent playerJoinEvent = new SuperheroPlayerJoinEvent(superheroPlayer.getSuperhero(), player);
            Bukkit.getPluginManager().callEvent(playerJoinEvent);
        }
    }

    public void unloadSuperheroPlayer(@NotNull Player player) {
        uuidToData.remove(player.getUniqueId());
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
            if (noPower.equals(superhero)) continue;
            newHero = superhero;
            break;
        }
        return newHero;
    }

    public void showHero(Player player, Superhero hero) {
        Component colouredName = MiniMessage.miniMessage().deserialize(hero.getColouredName());
        Component description = MiniMessage.miniMessage().deserialize(hero.getDescription());
        Title title = Title.title(colouredName, description, Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(5000), Duration.ofMillis(500)));
        Audience playerAudience = Superheroes.getBukkitAudiences().player(player);
        playerAudience.showTitle(title);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5F, 1F);
        Component heroGainedMessage = MiniMessage.miniMessage().deserialize(configHandler.getHeroGainedMessage(),
                Placeholder.component("hero", colouredName),
                Placeholder.unparsed("player", player.getDisplayName()));
        playerAudience.sendMessage(heroGainedMessage);
    }

    @Nullable
    public Superhero getSuperhero(String name) {
        return nameToSuperhero.get(name.toLowerCase());
    }

    public Superheroes getPlugin() {
        return superheroes;
    }

    public HashMap<String, Superhero> getNameToSuperhero() {
        return nameToSuperhero;
    }

    public Superhero getNoPower() {
        return noPower;
    }

    public HeroIOHandler getHeroIOHandler() {
        return heroIOHandler;
    }
}
