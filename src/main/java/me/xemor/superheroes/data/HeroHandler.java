/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
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
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class HeroHandler {
    private final HashMap<UUID, SuperheroPlayer> uuidToData = new HashMap<>();
    private final ConcurrentHashMap<UUID, CompletableFuture<SuperheroPlayer>> isProcessing = new ConcurrentHashMap<>();
    private final Superheroes superheroes;
    private final ConfigHandler configHandler;
    private Superhero noPower;
    private HeroIOHandler heroIOHandler;
    private List<String> disabledWorlds;
    private HashMap<String, Superhero> nameToSuperhero = new HashMap<>();

    public HeroHandler(Superheroes superheroes, ConfigHandler configHandler) {
        this.configHandler = configHandler;
        this.superheroes = superheroes;
        this.loadConfigItems();
    }

    public void loadConfigItems() {
        this.noPower = this.configHandler.getDefaultHero();
        this.disabledWorlds = this.configHandler.getDisabledWorlds();
    }

    public void registerHeroes(HashMap<String, Superhero> nameToSuperhero) {
        this.nameToSuperhero = nameToSuperhero;
        nameToSuperhero.put(this.noPower.getName().toLowerCase(), this.noPower);
    }

    public void setHeroesIntoMemory(HashMap<UUID, SuperheroPlayer> playerHeroes) {
        this.uuidToData.clear();
        this.uuidToData.putAll(playerHeroes);
    }

    public void handlePlayerData() {
        this.heroIOHandler = new HeroIOHandler();
        this.heroIOHandler.handlePlayerData();
    }

    public SuperheroPlayer getSuperheroPlayer(Player player) {
        return this.uuidToData.get(player.getUniqueId());
    }

    @NotNull
    public Superhero getSuperhero(Player player) {
        if (this.disabledWorlds.contains(player.getWorld().getName())) {
            return this.noPower;
        }
        SuperheroPlayer heroPlayer = this.uuidToData.get(player.getUniqueId());
        if (heroPlayer == null) {
            return this.noPower;
        }
        Superhero hero = heroPlayer.getSuperhero();
        if (player.getGameMode() == GameMode.SPECTATOR && !hero.hasSkill(Skill.getSkill("PHASE"))) {
            return this.noPower;
        }
        return hero;
    }

    @NotNull
    public Superhero getSuperhero(UUID uuid) {
        Player player = Bukkit.getPlayer((UUID) uuid);
        return this.getSuperhero(player);
    }

    public void setHeroInMemory(Player player, Superhero hero) {
        this.setHeroInMemory(player, hero, true);
    }

    public void setHeroInMemory(Player player, Superhero hero, boolean show) {
        SuperheroPlayer superheroPlayer = this.uuidToData.get(player.getUniqueId());
        if (superheroPlayer == null) {
            superheroPlayer = new SuperheroPlayer(player.getUniqueId(), hero, 0L);
            this.uuidToData.put(player.getUniqueId(), superheroPlayer);
        }
        Superhero currentHero = superheroPlayer.getSuperhero();
        PlayerChangedSuperheroEvent playerChangedHero = new PlayerChangedSuperheroEvent(player, hero, currentHero);
        Bukkit.getServer().getPluginManager().callEvent((Event) playerChangedHero);
        superheroPlayer.setSuperhero(hero);
        if (show) {
            this.showHero(player, hero);
        }
    }

    public void setHero(Player player, Superhero hero) {
        this.setHero(player, hero, true);
    }

    public void setHero(Player player, Superhero hero, boolean show) {
        this.setHeroInMemory(player, hero, show);
        this.heroIOHandler.saveSuperheroPlayerAsync(this.getSuperheroPlayer(player));
    }

    public void openHeroGUI(final Player player) {
        List<Superhero> allowedSuperheroes = this.nameToSuperhero.values().stream().filter(hero -> !this.configHandler.areHeroPermissionsRequired() || player.hasPermission(hero.getPermission())).toList();
        if (allowedSuperheroes.isEmpty()) {
            return;
        }
        int numberOfRows = (allowedSuperheroes.size() - 1) / 9 + 1;
        if (numberOfRows > 6) {
            Superheroes.getInstance().getLogger().severe("The hero GUI does not support more than 54 heroes.");
            return;
        }
        final ChestInterface<boolean[]> chestInterface = new ChestInterface<boolean[]>(this.configHandler.getGUIName(), numberOfRows, new boolean[]{false});
        for (Superhero superhero : allowedSuperheroes) {
            if (superhero.getIcon() == null) continue;
            chestInterface.getInventory().addItem(superhero.getIcon());
            chestInterface.getInteractions().addSimpleInteraction(superhero.getIcon(), p -> {
                if (this.getSuperheroPlayer(player).handleCooldown(player, Superheroes.getBukkitAudiences().player(player))) {
                    this.setHero(p, superhero);
                }
                chestInterface.getInteractions().getData()[0] = true;
            });
            if (this.configHandler.canCloseGUI()) continue;
            chestInterface.getInteractions().addCloseInteraction(p -> new BukkitRunnable() {

                public void run() {
                    if (!((boolean[]) chestInterface.getInteractions().getData())[0]) {
                        HeroHandler.this.openHeroGUI(player);
                    }
                }
            }.runTaskLater((Plugin) this.superheroes, 1L));
        }
        player.openInventory(chestInterface.getInventory());
    }

    public void loadSuperheroPlayer(@NotNull Player player) {
        CompletableFuture<SuperheroPlayer> future = this.heroIOHandler.loadSuperHeroPlayerAsync(player.getUniqueId());
        future.thenAccept(superheroPlayer -> Bukkit.getScheduler().runTask((Plugin) this.superheroes, () -> {
            Superhero superhero;
            if (superheroPlayer != null) {
                this.uuidToData.put(player.getUniqueId(), (SuperheroPlayer) superheroPlayer);
                superhero = superheroPlayer.getSuperhero();
            } else {
                superhero = this.configHandler.isPowerOnStartEnabled() ? this.getRandomHero(player) : this.noPower;
                if (this.configHandler.openGUIOnStart()) {
                    this.openHeroGUI(player);
                }
                this.setHero(player, superhero, this.configHandler.shouldShowHeroOnStart());
            }
            SuperheroPlayerJoinEvent playerJoinEvent = new SuperheroPlayerJoinEvent(superhero, player);
            Bukkit.getPluginManager().callEvent((Event) playerJoinEvent);
        }));
    }

    public void unloadSuperheroPlayer(@NotNull Player player) {
        this.uuidToData.remove(player.getUniqueId());
    }

    @Deprecated
    public Superhero getRandomHero(Player player) {
        ArrayList<Superhero> superheroes = new ArrayList<Superhero>(this.nameToSuperhero.values());
        Collections.shuffle(superheroes);
        Superhero newHero = this.noPower;
        for (Superhero superhero : superheroes) {
            if (this.configHandler.areHeroPermissionsRequired() && !player.hasPermission(superhero.getPermission()) || this.noPower.equals(superhero))
                continue;
            newHero = superhero;
            break;
        }
        return newHero;
    }

    public void showHero(Player player, Superhero hero) {
        Component colouredName = MiniMessage.miniMessage().deserialize(hero.getColouredName());
        Component description = MiniMessage.miniMessage().deserialize(hero.getDescription());
        Title title = Title.title(colouredName, description, Title.Times.times(Duration.ofMillis(500L), Duration.ofMillis(5000L), Duration.ofMillis(500L)));
        Audience playerAudience = Superheroes.getBukkitAudiences().player(player);
        playerAudience.showTitle(title);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5f, 1.0f);
        Component heroGainedMessage = MiniMessage.miniMessage().deserialize(this.configHandler.getHeroGainedMessage(), Placeholder.component("hero", colouredName), Placeholder.unparsed("player", player.getName()));
        playerAudience.sendMessage(heroGainedMessage);
    }

    @Nullable
    public Superhero getSuperhero(String name) {
        if (name == null) {
            return null;
        }
        return this.nameToSuperhero.get(name.toLowerCase());
    }

    public Superheroes getPlugin() {
        return this.superheroes;
    }

    public HashMap<String, Superhero> getNameToSuperhero() {
        return this.nameToSuperhero;
    }

    public Superhero getNoPower() {
        return this.noPower;
    }

    public HeroIOHandler getHeroIOHandler() {
        return this.heroIOHandler;
    }
}

