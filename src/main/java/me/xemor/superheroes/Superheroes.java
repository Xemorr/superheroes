package me.xemor.superheroes;

import me.creeves.particleslibrary.ParticlesLibrary;
import me.xemor.configurationdata.ConfigurationData;
import me.xemor.skillslibrary2.conditions.Conditions;
import me.xemor.superheroes.commands.HeroCommand;
import me.xemor.superheroes.conditions.SuperheroCondition;
import me.xemor.superheroes.data.ConfigHandler;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.reroll.RerollHandler;
import me.xemor.superheroes.sentry.SentryInitializer;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.implementations.*;
import me.xemor.userinterface.ChestHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import me.xemor.foliahacks.FoliaHacks;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.io.ObjectInputFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public final class Superheroes extends JavaPlugin implements Listener {
    private ConfigHandler configHandler;
    private HeroHandler heroHandler;
    private RerollHandler rerollHandler;
    private static BukkitAudiences bukkitAudiences;
    private boolean hasSkillsLibrary;
    private static Superheroes superheroes;
    private WorldGuardSupport worldGuardSupport;
    private static FoliaHacks foliaHacks;

    public static Superheroes getInstance() {
        return superheroes;
    }

    public static BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

    public static GracefulScheduling getScheduling() {
        return foliaHacks.getScheduling();
    }

    @Override
    public void onLoad() {
        if (this.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            WorldGuardSupport.setupFlag();
        }
    }

    @Override
    public void onEnable() {
        SentryInitializer.initSentry("https://a5dfe8c79f9c3dad331ebdcb8923066a@o4505846670753792.ingest.sentry.io/4505846683992064", this);
        superheroes = this;
        ParticlesLibrary.registerParticlesLibrary(this);
        ConfigurationData.setup(this);
        foliaHacks = new FoliaHacks(this);
        this.saveDefaultConfig();
        this.configHandler = new ConfigHandler(this);
        this.heroHandler = new HeroHandler(this, this.configHandler);
        this.registerSkills();
        this.getServer().getPluginManager().registerEvents(this, this);
        HeroCommand heroCommand = new HeroCommand(this.heroHandler);
        PluginCommand command = this.getCommand("hero");
        command.setExecutor(heroCommand);
        command.setTabCompleter(heroCommand);
        this.handleMetrics();
        this.plusUltraAdvertisement();
        this.checkForNewUpdate();
        bukkitAudiences = BukkitAudiences.create(this);
        this.hasSkillsLibrary = Bukkit.getPluginManager().isPluginEnabled("SkillsLibrary2");
        if (this.hasSkillsLibrary) {
            this.runSkillsLibraryChanges();
        }
        this.handleAliases(heroCommand, command);
        this.registerUserInterfaces();
        if (this.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuardSupport = new WorldGuardSupport();
            this.getServer().getPluginManager().registerEvents(worldGuardSupport, this);
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e) {
        this.configHandler.loadSuperheroes(this.heroHandler);
        this.rerollHandler = new RerollHandler();
        this.getServer().getPluginManager().registerEvents(this.rerollHandler, this);
        this.heroHandler.handlePlayerData();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        this.heroHandler.loadSuperheroPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        this.heroHandler.unloadSuperheroPlayer(e.getPlayer());
    }

    public void registerSkills() {
        SkillImplementation[] skills = new SkillImplementation[]{new DamageModifierSkill(this.heroHandler), new FlightSkill(this.heroHandler), new AttributeSkill(this.heroHandler), new PotionEffectSkill(this.heroHandler), new InstantBreak(this.heroHandler), new LightSkill(this.heroHandler), new NoHungerSkill(this.heroHandler), new DamageResistanceSkill(this.heroHandler), new SlimeSkill(this.heroHandler), new SneakingPotionSkill(this.heroHandler), new EggLayerSkill(this.heroHandler), new WalkerSkill(this.heroHandler), new AuraSkill(this.heroHandler), new PickpocketSkill(this.heroHandler), new StrongmanSkill(this.heroHandler), new PhaseSkill(this.heroHandler), new SlamSkill(this.heroHandler), new EraserSkill(this.heroHandler), new CraftingSkill(this.heroHandler), new TeleportSkill(this.heroHandler), new SummonSkill(this.heroHandler), new DecoySkill(this.heroHandler), new PotionGifterSkill(this.heroHandler), new ConsumeSkill(this.heroHandler), new BlockRaySkill(this.heroHandler), new OHKOSkill(this.heroHandler), new RepulsionSkill(this.heroHandler), new CreeperSkill(this.heroHandler), new GiveItemSkill(this.heroHandler), new GunSkill(this.heroHandler), new SneakSkill(this.heroHandler), new ShieldSkill(this.heroHandler), new SpellSkill(this.heroHandler), new ThrowerSkill(this.heroHandler), new ConvertItemSkill(this.heroHandler), new ConvertBlockSkill(this.heroHandler), new RemoteDetonationSkill(this.heroHandler), new BlockDropsSkill(this.heroHandler), new ConvertDropsSkill(this.heroHandler), new LifestealSkill(this.heroHandler), new DamagePotionSkill(this.heroHandler), new WeatherDamageSkill(this.heroHandler), new HeartStealSkill(this.heroHandler), new KillPotionSkill(this.heroHandler), new ClimbSkill(this.heroHandler)};
        for (SkillImplementation skill : skills) {
            this.getServer().getPluginManager().registerEvents(skill, this);
        }
    }

    public void registerUserInterfaces() {
        this.getServer().getPluginManager().registerEvents(new ChestHandler(), this);
    }

    public void runSkillsLibraryChanges() {
        Conditions.register("HERO", SuperheroCondition.class);
    }

    public void plusUltraAdvertisement() {
        getScheduling().globalRegionalScheduler().runAtFixedRate(() -> {
            if (Bukkit.getOnlinePlayers().size() > 25 && Bukkit.getPluginManager().getPlugin("SuperheroesPlusUltra") == null) {
                Superheroes.this.getLogger().info("This server has quite a few players online! You may benefit from my premium addon SuperheroesPlusUltra");
                Superheroes.this.getLogger().info("This plugin adds a few extra default heroes, unlocks new powers in existing heroes");
                Superheroes.this.getLogger().info("adds skins to many heroes, adds skript compatibility, and even lets you write CUSTOM skills for your heroes!");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasPermission("superheroes.notify")) continue;
                    player.sendMessage("This server has quite a few players online! You may benefit from my premium addon SuperheroesPlusUltra");
                    player.sendMessage("This plugin adds a few extra default heroes, unlocks new powers in existing heroes");
                    player.sendMessage("adds skins to many heroes, adds skript compatibility, and even lets you write CUSTOM skills for your heroes!");
                }
            }
        }, 3000L, 469000L);
    }

    public void checkForNewUpdate() {
        UpdateChecker.init(this, 79766);
        getScheduling().globalRegionalScheduler().runAtFixedRate(() -> {
                if (UpdateChecker.isInitialized()) {
                    UpdateChecker updateChecker = UpdateChecker.get();
                    updateChecker.requestUpdateCheck().thenAccept(result -> {
                        if (result.requiresUpdate()) {
                            Superheroes.this.getLogger().warning("This server is still running " + Superheroes.this.getDescription().getVersion() + " of Superheroes");
                            Superheroes.this.getLogger().warning("The latest version is " + result.getNewestVersion());
                            Superheroes.this.getLogger().warning("Updating is important to ensure there are not any bugs or vulnerabilities.");
                            Superheroes.this.getLogger().warning("As well as ensuring your players have the best time when using the superheroes!");
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (!player.hasPermission("superheroes.notify")) continue;
                                player.sendMessage("This server is still running " + Superheroes.this.getDescription().getVersion() + " of Superheroes");
                                player.sendMessage("The latest version is " + result.getNewestVersion());
                                player.sendMessage("Updating is important to ensure there are not any bugs or vulnerabilities.");
                                player.sendMessage("As well as ensuring your players have the best time when using the superheroes!");
                            }
                        }
                    });
                }
            }, 6000L, 432000L
        );
    }

    public void onDisable() {
        bukkitAudiences.close();
        superheroes.getHeroHandler().getHeroIOHandler().shutdown();
    }

    public void handleMetrics() {
        Metrics metrics = new Metrics(this, 8671);
        if (!metrics.isEnabled()) {
            this.getLogger().log(Level.WARNING, "[Superheroes] You have disabled bstats, this is very sad :(");
        }
        metrics.addCustomChart(new Metrics.AdvancedPie("players_using_each_superhero", () -> {
            HashMap<String, Integer> valueMap = new HashMap<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String powerName = this.heroHandler.getSuperhero(player).getName();
                int currentCount = valueMap.getOrDefault(powerName, 0);
                valueMap.put(powerName, ++currentCount);
            }
            return valueMap;
        }));
        metrics.addCustomChart(new Metrics.AdvancedPie("players_using_each_skill", () -> {
            HashMap<String, Integer> valueMap = new HashMap<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                Collection<Integer> skills = this.heroHandler.getSuperhero(player).getSkills();
                for (int skill : skills) {
                    int currentCount = valueMap.getOrDefault(Skill.getName(skill), 0);
                    valueMap.put(Skill.getName(skill), ++currentCount);
                }
            }
            return valueMap;
        }));
        metrics.addCustomChart(new Metrics.AdvancedPie("superheroes_using_each_skill", () -> {
            HashMap<String, Integer> valueMap = new HashMap<>();
            for (Superhero superhero : Superheroes.getInstance().getHeroHandler().getNameToSuperhero().values()) {
                Collection<Integer> skills = superhero.getSkills();
                for (int skill : skills) {
                    int currentCount = valueMap.getOrDefault(Skill.getName(skill), 0);
                    valueMap.put(Skill.getName(skill), ++currentCount);
                }
            }
            return valueMap;
        }));
        metrics.addCustomChart(new Metrics.SimplePie("superheroes_plus_ultra_usage", () -> Bukkit.getPluginManager().getPlugin("SuperheroesPlusUltra") != null ? "Yes" : "No"));
        metrics.addCustomChart(new Metrics.SimplePie("has_worldguard", () -> Bukkit.getPluginManager().getPlugin("WorldGuard") != null ? "Yes" : "No"));
    }

    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    public HeroHandler getHeroHandler() {
        return this.heroHandler;
    }

    public boolean hasSkillsLibrary() {
        return this.hasSkillsLibrary;
    }

    public RerollHandler getRerollHandler() {
        return this.rerollHandler;
    }

    private void handleAliases(final HeroCommand heroCommand, PluginCommand command) {
        List<String> commandAliases = this.configHandler.getCommandAliases();
        if (!commandAliases.isEmpty()) {
            BukkitCommand aliasCommand = new BukkitCommand(commandAliases.get(0)) {
                public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                    return heroCommand.onCommand(sender, this, commandLabel, args);
                }

                @NotNull
                public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
                    return heroCommand.onTabComplete(sender, this, alias, args);
                }
            };
            aliasCommand.setDescription(command.getDescription());
            aliasCommand.setAliases(commandAliases.subList(1, commandAliases.size()));
            CommandMap commandMap = Superheroes.foliaHacks.getMorePaperLib().commandRegistration().getServerCommandMap();
            commandMap.register("superheroes", aliasCommand);
        }
    }
}

