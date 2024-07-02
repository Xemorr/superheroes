package me.xemor.superheroes.data;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.events.PlayerAsyncCheckSuperheroEvent;
import me.xemor.superheroes.events.SuperheroPlayerJoinEvent;
import me.xemor.superheroes.reroll.RerollGroup;
import me.xemor.superheroes.skills.Skill;
import me.xemor.userinterface.ChestInterface;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class HeroHandler {
    private final ConcurrentHashMap<UUID, SuperheroPlayer> uuidToData = new ConcurrentHashMap<>();
    private final Superheroes superheroes;
    private final ConfigHandler configHandler;
    private Superhero noPower;
    private HeroIOHandler heroIOHandler;
    private List<String> disabledWorlds;
    private HashMap<String, Superhero> nameToSuperhero = new HashMap<>();

    public HeroHandler(Superheroes superheroes, ConfigHandler configHandler) {
        this.configHandler = configHandler;
        this.superheroes = superheroes;
        heroIOHandler = new HeroIOHandler();
        loadConfigItems();
    }

    public void loadConfigItems() {
        noPower = configHandler.getDefaultHero();
        disabledWorlds = configHandler.getDisabledWorlds();
    }

    public void registerHeroes(HashMap<String, Superhero> nameToSuperhero) {
        this.nameToSuperhero = nameToSuperhero;
        nameToSuperhero.put(noPower.getName().toLowerCase(), noPower);
    }

    public void setHeroesIntoMemory(HashMap<UUID, SuperheroPlayer> playerHeroes) {
        uuidToData.clear();
        uuidToData.putAll(playerHeroes);
    }

    public void handlePlayerData() {
        heroIOHandler.handlePlayerData();
    }

    public SuperheroPlayer getSuperheroPlayer(Player player) {
        return uuidToData.get(player.getUniqueId());
    }

    public SuperheroPlayer getSuperheroPlayer(UUID uniqueId) {
        return uuidToData.get(uniqueId);
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
        PlayerAsyncCheckSuperheroEvent event = new PlayerAsyncCheckSuperheroEvent(hero, player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event.getSuperhero();
    }

    @NotNull
    public Superhero getSuperhero(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        return getSuperhero(player);
    }

    public void setHeroInMemory(Player player, Superhero hero) {
        setHeroInMemory(player, hero, true);
    }

    public void setHeroInMemory(Player player, Superhero hero, boolean show) {
        setHeroInMemory(player, hero, show, PlayerChangedSuperheroEvent.Cause.OTHER);
    }

    public void setHeroInMemory(Player player, Superhero hero, boolean show, PlayerChangedSuperheroEvent.Cause cause) {
        setHeroInMemory(player, hero, show, cause, false);
    }

    private void setHeroInMemory(Player player, Superhero hero, boolean show, PlayerChangedSuperheroEvent.Cause cause, boolean wasWrittenToDisk) {
        SuperheroPlayer superheroPlayer = uuidToData.get(player.getUniqueId());
        if (superheroPlayer == null) {
            superheroPlayer = new SuperheroPlayer(player.getUniqueId(), hero, 0L);
            uuidToData.put(player.getUniqueId(), superheroPlayer);
        }
        Superhero currentHero = superheroPlayer.getSuperhero();
        PlayerChangedSuperheroEvent playerChangedHero = new PlayerChangedSuperheroEvent(player, hero, currentHero, cause, wasWrittenToDisk);
        Bukkit.getServer().getPluginManager().callEvent(playerChangedHero);
        if (!playerChangedHero.isCancelled()) {
            superheroPlayer.setSuperhero(playerChangedHero.getNewHero());
            if (show) {
                showHero(player, playerChangedHero.getNewHero());
            }
        }
    }

    public void setHero(Player player, Superhero hero) {
        setHero(player, hero, true);
    }

    public void setHero(Player player, Superhero hero, boolean show) {
        setHero(player, hero, show, PlayerChangedSuperheroEvent.Cause.OTHER);
    }

    public void setHero(Player player, Superhero hero, boolean show, PlayerChangedSuperheroEvent.Cause cause) {
        setHeroInMemory(player, hero, show, cause, true);
        heroIOHandler.saveSuperheroPlayerAsync(getSuperheroPlayer(player));
    }

    public void openHeroGUI(final Player player) {
        List<Superhero> allowedSuperheroes = nameToSuperhero.values().stream().filter(hero -> !Superheroes.getInstance().getRerollHandler().doesHeroRequirePermissions() || player.hasPermission(hero.getPermission())).toList();
        if (allowedSuperheroes.isEmpty()) {
            return;
        }
        int numberOfRows = (allowedSuperheroes.size() - 1) / 9 + 1;
        if (numberOfRows > 6) {
            Superheroes.getInstance().getLogger().severe("The hero GUI does not support more than 54 heroes.");
            return;
        }
        final ChestInterface<boolean[]> chestInterface = new ChestInterface<>(configHandler.getGUIName(), numberOfRows, new boolean[]{false});
        for (Superhero superhero : allowedSuperheroes) {
            if (superhero.getIcon() == null) continue;
            chestInterface.getInventory().addItem(superhero.getIcon());
            chestInterface.getInteractions().addSimpleInteraction(superhero.getIcon(), p -> {
                if (getSuperheroPlayer(player).handleCooldown(player, Superheroes.getBukkitAudiences().player(player))) {
                    setHero(p, superhero);
                }
                chestInterface.getInteractions().getData()[0] = true;
                player.closeInventory();
            });
            if (this.configHandler.canCloseGUI()) continue;
            chestInterface.getInteractions().addCloseInteraction(p -> Superheroes.getScheduling().entitySpecificScheduler(player)
                    .runDelayed(() -> {
                        if (!((boolean[]) chestInterface.getInteractions().getData())[0]) {
                            openHeroGUI(player);
                        }
                    }, () -> {}, 1L)
            );
        }
        player.openInventory(chestInterface.getInventory());
    }

    public void loadSuperheroPlayer(@NotNull Player player) {
        CompletableFuture<SuperheroPlayer> future = heroIOHandler.loadSuperHeroPlayerAsync(player.getUniqueId());
        future.thenAccept(superheroPlayer -> Superheroes.getScheduling().entitySpecificScheduler(player).run(() -> {
            Superhero superhero;
            if (superheroPlayer != null) {
                uuidToData.put(player.getUniqueId(), superheroPlayer);
                superhero = superheroPlayer.getSuperhero();
            } else {
                RerollGroup defaultGroup = Superheroes.getInstance().getRerollHandler().getWeightedHeroes("default");
                if (defaultGroup == null) throw new IllegalStateException("Default Reroll Group not initialized for some reason!");
                superhero = configHandler.isPowerOnStartEnabled() ? defaultGroup.chooseHero(player) : noPower;
                if (configHandler.openGUIOnStart()) {
                    openHeroGUI(player);
                }
                setHero(player, superhero, configHandler.shouldShowHeroOnStart());
            }
            SuperheroPlayerJoinEvent playerJoinEvent = new SuperheroPlayerJoinEvent(superhero, player);
            Bukkit.getPluginManager().callEvent(playerJoinEvent);
        }, () -> {}));
    }

    public void unloadSuperheroPlayer(@NotNull Player player) {
        uuidToData.remove(player.getUniqueId());
    }

    @Deprecated
    public Superhero getRandomHero(Player player) {
        ArrayList<Superhero> superheroes = new ArrayList<>(nameToSuperhero.values());
        Collections.shuffle(superheroes);
        Superhero newHero = noPower;
        for (Superhero superhero : superheroes) {
            if (Superheroes.getInstance().getRerollHandler().doesHeroRequirePermissions() && !player.hasPermission(superhero.getPermission()) || noPower.equals(superhero))
                continue;
            newHero = superhero;
            break;
        }
        return newHero;
    }

    public void showHero(Player player, Superhero hero) {
        Component colouredName = MiniMessage.miniMessage().deserialize(hero.getColouredName());
        Component description = MiniMessage.miniMessage().deserialize(hero.getDescription());
        Audience playerAudience = Superheroes.getBukkitAudiences().player(player);
        /*
        Title title = Title.title(colouredName, description, Title.Times.times(Duration.ofMillis(500L), Duration.ofMillis(5000L), Duration.ofMillis(500L)));
        playerAudience.showTitle(title);

        Adventure Developers aren't on top of maintaining adventure platform bukkit, so I will use a work around to prevent sendTitle from breaking every 3 milliseconds.
         */
        String colouredNameSpigot = LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build().serialize(colouredName);
        String descriptionSpigot = LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build().serialize(description);
        player.sendTitle(colouredNameSpigot, descriptionSpigot, 10, 100, 10);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5f, 1.0f);
        Component heroGainedMessage = MiniMessage.miniMessage().deserialize(configHandler.getHeroGainedMessage(), Placeholder.component("hero", colouredName), Placeholder.unparsed("player", player.getName()));
        playerAudience.sendMessage(heroGainedMessage);
    }

    @Nullable
    public Superhero getSuperhero(String name) {
        if (name == null) {
            return null;
        }
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

