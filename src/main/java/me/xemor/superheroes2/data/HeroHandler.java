package me.xemor.superheroes2.data;

import de.themoep.minedown.adventure.MineDown;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.storage.LegacyStorage;
import me.xemor.superheroes2.data.storage.MySQLStorage;
import me.xemor.superheroes2.data.storage.Storage;
import me.xemor.superheroes2.data.storage.YAMLStorage;
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
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class HeroHandler implements Listener {

    private final HashMap<UUID, SuperheroPlayer> uuidToData = new HashMap<>();
    private HashMap<String, Superhero> nameToSuperhero = new HashMap<>();
    Superheroes2 superheroes2;
    private final ConfigHandler configHandler;
    private final Superhero noPower = new Superhero("NOPOWER", ChatColor.translateAlternateColorCodes('&', "&e&lNOPOWER"), "They have no power");
    Storage storage;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        loadSuperheroPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        uuidToData.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBroken(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (!e.isCancelled() && e.isDropItems()) {
            Collection<ItemStack> drops = e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer());
            HeroBlockBreakEvent heroBlockBreakEvent = new HeroBlockBreakEvent(e.getBlock(), e.getPlayer(), drops);
            heroBlockBreakEvent.callEvent();
            if (!heroBlockBreakEvent.isCancelled()) {
                if (heroBlockBreakEvent.isDropItems()) {
                    for (ItemStack drop : drops) {
                        e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
                    }
                }
            }
            e.setDropItems(false);
        }
    }

    public HeroHandler(Superheroes2 superheroes2, ConfigHandler configHandler) {
        this.configHandler = configHandler;
        this.superheroes2 = superheroes2;
    }

    public CompletableFuture<Void> importFiles() {
        YAMLStorage yamlStorage = new YAMLStorage(this);
        List<SuperheroPlayer> superheroPlayers = yamlStorage.exportSuperheroPlayers();
        return storage.importSuperheroPlayers(superheroPlayers);
    }

    public void exportFiles() {
        List<SuperheroPlayer> superheroPlayers = storage.exportSuperheroPlayers();
        YAMLStorage yamlStorage = new YAMLStorage(this);
        yamlStorage.importSuperheroPlayers(superheroPlayers);
    }

    public void handlePlayerData() {
        String databaseType = configHandler.getDatabaseType();
        if (databaseType.equalsIgnoreCase("LEGACY")) {
            LegacyStorage legacy = new LegacyStorage(this);
            List<SuperheroPlayer> values = legacy.exportSuperheroPlayers();
            File file = legacy.getCurrentDataFile();
            file.renameTo(new File(superheroes2.getDataFolder(), "old_data.yml"));
            storage = new YAMLStorage(this);
            for (SuperheroPlayer superheroPlayer: values) {
                storage.saveSuperheroPlayer(superheroPlayer);
            }
            configHandler.setDatabaseType("YAML");
        }
        else if (databaseType.equalsIgnoreCase("YAML")) {
            storage = new YAMLStorage(this);
        }
        else if (databaseType.equalsIgnoreCase("MySQL")) {
            storage = new MySQLStorage(this);
        }
        else {
            Bukkit.getLogger().severe("Invalid database type specified!");
        }
    }

    public void registerHeroes(HashMap<String, Superhero> nameToSuperhero) {
        this.nameToSuperhero = nameToSuperhero;
        nameToSuperhero.put("NOPOWER", noPower);
    }

    public void setHeroesIntoMemory(HashMap<UUID, SuperheroPlayer> playerHeroes) {
        this.uuidToData.clear();
        this.uuidToData.putAll(playerHeroes);
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
     * @param player
     * @param hero
     */
    public void setHeroInMemory(Player player, Superhero hero) {
        setHeroInMemory(player, hero, true);
    }

    public void setHeroInMemory(Player player, Superhero hero, boolean show) {
        SuperheroPlayer superheroPlayer = uuidToData.get(player.getUniqueId());
        if (superheroPlayer == null) {
            superheroes2.getLogger().severe("Line 3 of setHeroInMemory, superheroPlayer is null");
            superheroes2.getLogger().severe("This occurs if a player's superhero data has not been loaded correctly");
            superheroes2.getLogger().severe("This should not be happening! Join Xemor's Server discord and report this bug!");
            superheroes2.getLogger().severe("If you have used /reload, then that is the cause for this bug. Please refrain from using this in future and restart your server.");
            superheroes2.getLogger().severe("Defaulting to giving the player noPower to allow the server to continue as normal!");
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
        saveSuperheroPlayer(getSuperheroPlayer(player));
    }

    public void loadSuperheroPlayer(@NotNull Player player) {
        uuidToData.put(player.getUniqueId(), new SuperheroPlayer(player.getUniqueId(), noPower, 0));
        CompletableFuture<SuperheroPlayer> future = storage.loadSuperheroPlayerAsync(player.getUniqueId());
        future.thenAccept((superheroPlayer) -> Bukkit.getScheduler().runTask(superheroes2, () -> {
            if (superheroPlayer == null) {
                Superhero superhero;
                if (configHandler.isPowerOnStartEnabled()) {
                    superhero = getRandomHero(player);
                }
                else {
                    superhero = noPower;
                }
                if (configHandler.shouldShowHeroOnStart()) {
                    showHero(player, superhero);
                }
                uuidToData.put(player.getUniqueId(), new SuperheroPlayer(player.getUniqueId(), superhero, 0));
            }
            else {
                uuidToData.put(player.getUniqueId(), superheroPlayer);
            }
        }));
    }

    public void saveSuperheroPlayer(SuperheroPlayer superheroPlayer) {
        storage.saveSuperheroPlayerAsync(superheroPlayer);
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

    public Superhero getNoPower() {
        return noPower;
    }
}
