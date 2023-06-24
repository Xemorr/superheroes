package me.xemor.superheroes.data;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.events.SuperheroPlayerJoinEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.userinterface.ChestInterface;
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
            superheroPlayer = new SuperheroPlayer(player.getUniqueId(), hero, 0);
            uuidToData.put(player.getUniqueId(), superheroPlayer);
        }
        Superhero currentHero = superheroPlayer.getSuperhero();
        PlayerChangedSuperheroEvent playerChangedHero = new PlayerChangedSuperheroEvent(player, hero, currentHero);
        Bukkit.getServer().getPluginManager().callEvent(playerChangedHero);
        superheroPlayer.setSuperhero(hero);
        if (show) {
            showHero(player, hero);
        }
    }

    public void setHero(Player player, Superhero hero) {
        setHero(player, hero, true);
    }

    public void setHero(Player player, Superhero hero, boolean show) {
        setHeroInMemory(player, hero, show);
        heroIOHandler.saveSuperheroPlayerAsync(getSuperheroPlayer(player));
    }

    public void openHeroGUI(Player player) {
        List<Superhero> allowedSuperheroes = nameToSuperhero.values().stream().filter((hero) -> !configHandler.areHeroPermissionsRequired() || player.hasPermission(hero.getPermission())).toList();
        if (allowedSuperheroes.isEmpty()) return;
        // without the subtract one from the size it always has one more row than it should
        // the + 1 is so you don't get silliness like 0 rows
        int numberOfRows = ((allowedSuperheroes.size() - 1) / 9) + 1;
        if (numberOfRows > 6) {
            Superheroes.getInstance().getLogger().severe("The hero GUI does not support more than 54 heroes.");
            return;
        }
        ChestInterface<boolean[]> chestInterface = new ChestInterface<>(configHandler.getGUIName(), numberOfRows, new boolean[]{false});
        for (Superhero superhero : allowedSuperheroes) {
            if (superhero.getIcon() == null) continue;
            chestInterface.getInventory().addItem(superhero.getIcon());
            chestInterface.getInteractions().addSimpleInteraction(superhero.getIcon(), (p) -> {
                if (getSuperheroPlayer(player).handleCooldown(player, Superheroes.getBukkitAudiences().player(player))) {
                    setHero(p, superhero);
                }
                chestInterface.getInteractions().getData()[0] = true;
            });
            if (!configHandler.canCloseGUI()) {
                chestInterface.getInteractions().addCloseInteraction((p) -> new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!chestInterface.getInteractions().getData()[0]) {
                            openHeroGUI(player);
                        }
                    }
                }.runTaskLater(superheroes, 1L));
            }
        }
        player.openInventory(chestInterface.getInventory());
    }

    public void loadSuperheroPlayer(@NotNull Player player) {
        CompletableFuture<SuperheroPlayer> future = heroIOHandler.loadSuperHeroPlayerAsync(player.getUniqueId());
        future.thenAccept((superheroPlayer) -> Bukkit.getScheduler().runTask(superheroes, () -> {
            Superhero superhero;
            if (superheroPlayer != null) {
                uuidToData.put(player.getUniqueId(), superheroPlayer);
                superhero = superheroPlayer.getSuperhero();
            }
            else {
                if (configHandler.isPowerOnStartEnabled()) {
                    superhero = getRandomHero(player);
                } else {
                    superhero = noPower;
                }
                if (configHandler.openGUIOnStart()) {
                    openHeroGUI(player);
                }
                setHero(player, superhero, configHandler.shouldShowHeroOnStart());
            }
            SuperheroPlayerJoinEvent playerJoinEvent = new SuperheroPlayerJoinEvent(superhero, player);
            Bukkit.getPluginManager().callEvent(playerJoinEvent);
        }));
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
                Placeholder.unparsed("player", player.getName()));
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
