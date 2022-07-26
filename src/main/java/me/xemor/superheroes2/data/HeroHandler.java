package me.xemor.superheroes2.data;

import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class HeroHandler {

    private final HashMap<UUID, SuperheroPlayer> uuidToData = new HashMap<>();
    private HashMap<String, Superhero> nameToSuperhero = new HashMap<>();
    private final Superheroes2 superheroes2;
    private final ConfigHandler configHandler;
    private final static LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build();
    private final Superhero noPower = new Superhero("NOPOWER", "<yellow><b>NOPOWER", "They have no power");
    private HeroIOHandler heroIOHandler;

    public HeroHandler(Superheroes2 superheroes2, ConfigHandler configHandler) {
        this.configHandler = configHandler;
        this.superheroes2 = superheroes2;
    }

    public void registerHeroes(HashMap<String, Superhero> nameToSuperhero) {
        this.nameToSuperhero = nameToSuperhero;
        nameToSuperhero.put("nopower", noPower);
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

    public void loadSuperheroPlayer(@NotNull Player player) {
        uuidToData.put(player.getUniqueId(), new SuperheroPlayer(player.getUniqueId(), noPower, 0));
        CompletableFuture<SuperheroPlayer> future = heroIOHandler.loadSuperHeroPlayerAsync(player.getUniqueId());
        future.thenAccept((superheroPlayer) -> Bukkit.getScheduler().runTask(superheroes2, () -> {
            if (superheroPlayer == null) {
                Superhero superhero;
                if (configHandler.isPowerOnStartEnabled()) {
                    superhero = getRandomHero(player);
                } else {
                    superhero = noPower;
                }
                setHero(player, superhero, configHandler.shouldShowHeroOnStart());
            } else {
                uuidToData.put(player.getUniqueId(), superheroPlayer);
            }
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
            newHero = superhero;
            break;
        }
        return newHero;
    }

    public void showHero(Player player, Superhero hero) {
        Component colouredName = MiniMessage.miniMessage().deserialize(hero.getColouredName());
        Component description = MiniMessage.miniMessage().deserialize(hero.getDescription());
        Title title = Title.title(colouredName, description, Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(5000), Duration.ofMillis(500)));
        Audience playerAudience = Superheroes2.getBukkitAudiences().player(player);
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

    public Superheroes2 getPlugin() {
        return superheroes2;
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
