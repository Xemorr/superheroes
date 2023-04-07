package me.xemor.superheroes;

import me.xemor.skillslibrary2.conditions.Conditions;
import me.xemor.superheroes.commands.HeroCommand;
import me.xemor.superheroes.commands.Reroll;
import me.xemor.superheroes.commands.TextConvert;
import me.xemor.superheroes.conditions.SuperheroCondition;
import me.xemor.superheroes.data.ConfigHandler;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.implementations.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.io.FileUtils;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

public final class Superheroes extends JavaPlugin implements Listener {

    private ConfigHandler configHandler;
    private HeroHandler heroHandler;
    private static BukkitAudiences bukkitAudiences;
    private boolean hasSkillsLibrary;
    private static Superheroes superheroes;


    @Override
    public void onEnable() {
        // Plugin startup logic
        superheroes = this;
        saveDefaultConfig();
        boolean hasConvertedSuperheroes2Folder = getConfig().getBoolean("movedtosuperheroespluginname", false);
        if (!hasConvertedSuperheroes2Folder) convertFromSuperheroes2Folder();
        configHandler = new ConfigHandler(this);
        heroHandler = new HeroHandler(this, configHandler);
        registerSkills();
        Reroll reroll = new Reroll(heroHandler, configHandler);
        this.getServer().getPluginManager().registerEvents(reroll, this);
        this.getServer().getPluginManager().registerEvents(this, this);
        HeroCommand heroCommand = new HeroCommand(heroHandler, reroll);
        PluginCommand command = this.getCommand("hero");
        command.setExecutor(heroCommand);
        command.setTabCompleter(heroCommand);
        handleMetrics();
        plusUltraAdvertisement();
        checkForNewUpdate();
        bukkitAudiences = BukkitAudiences.create(this);
        hasSkillsLibrary = Bukkit.getPluginManager().isPluginEnabled("SkillsLibrary2");
        if (hasSkillsLibrary) runSkillsLibraryChanges();
        handleAliases(heroCommand, command);
    }

    public void convertFromSuperheroes2Folder() {
        File oldDataFolder = new File(getDataFolder().getParentFile(), "Superheroes2");
        if (oldDataFolder.exists()) {
            try {
                FileUtils.copyDirectory(oldDataFolder, getDataFolder());
                reloadConfig();
                getConfig().set("movedtosuperheroespluginname", true);
                boolean textconvert = getConfig().getBoolean("textconvert", false);
                if (!textconvert) { TextConvert.fixFiles(getDataFolder()); getConfig().set("textconvert", true);}
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            getConfig().set("movedtosuperheroespluginname", true);
        }
    }

    public void runSkillsLibraryChanges() {
        Conditions.register("HERO", SuperheroCondition.class);
    }

    public static BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

    @EventHandler
    public void onLoad(ServerLoadEvent e) {
        configHandler.loadSuperheroes(heroHandler);
        heroHandler.handlePlayerData();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        heroHandler.loadSuperheroPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        heroHandler.unloadSuperheroPlayer(e.getPlayer());
    }

    public void registerSkills() {
        SkillImplementation[] skills = new SkillImplementation[]{
                new PotionEffectSkill(heroHandler),
                new InstantBreak(heroHandler),
                new LightSkill(heroHandler),
                new NoHungerSkill(heroHandler),
                new DamageResistanceSkill(heroHandler),
                new SlimeSkill(heroHandler),
                new SneakingPotionSkill(heroHandler),
                new EggLayerSkill(heroHandler),
                new WalkerSkill(heroHandler),
                new AuraSkill(heroHandler),
                new PickpocketSkill(heroHandler),
                new StrongmanSkill(heroHandler),
                new PhaseSkill(heroHandler),
                new SlamSkill(heroHandler),
                new EraserSkill(heroHandler),
                new CraftingSkill(heroHandler),
                new TeleportSkill(heroHandler),
                new SummonSkill(heroHandler),
                new DecoySkill(heroHandler),
                new PotionGifterSkill(heroHandler),
                new ConsumeSkill(heroHandler),
                new BlockRaySkill(heroHandler),
                new OHKOSkill(heroHandler),
                new RepulsionSkill(heroHandler),
                new CreeperSkill(heroHandler),
                new GiveItemSkill(heroHandler),
                new GunSkill(heroHandler),
                new SneakSkill(heroHandler),
                new ShieldSkill(heroHandler),
                new SpellSkill(heroHandler),
                new ThrowerSkill(heroHandler),
                new ConvertItemSkill(heroHandler),
                new ConvertBlockSkill(heroHandler),
                new RemoteDetonationSkill(heroHandler),
                new BlockDropsSkill(heroHandler),
                new ConvertDropsSkill(heroHandler),
                new LifestealSkill(heroHandler),
                new DamagePotionSkill(heroHandler),
                new WeatherDamageSkill(heroHandler),
                new HeartStealSkill(heroHandler),
                new KillPotionSkill(heroHandler),
                new ClimbSkill(heroHandler)
        };
        for (SkillImplementation skill : skills) {
            this.getServer().getPluginManager().registerEvents(skill, this);
        }
    }

    public void plusUltraAdvertisement() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() > 25 && Bukkit.getPluginManager().getPlugin("SuperheroesPlusUltra") == null) {
                    getLogger().info("This server has quite a few players online! You may benefit from my premium addon SuperheroesPlusUltra");
                    getLogger().info("This plugin adds a few extra default heroes, unlocks new powers in existing heroes");
                    getLogger().info("adds skins to many heroes, adds skript compatibility, and even lets you write CUSTOM skills for your heroes!");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission("superheroes.notify")) {
                            player.sendMessage("This server has quite a few players online! You may benefit from my premium addon SuperheroesPlusUltra");
                            player.sendMessage("This plugin adds a few extra default heroes, unlocks new powers in existing heroes");
                            player.sendMessage("adds skins to many heroes, adds skript compatibility, and even lets you write CUSTOM skills for your heroes!");
                        }
                    }
                }
            }
        }.runTaskTimer(this, 3000L, 469000L);
    }

    public void checkForNewUpdate() {
        UpdateChecker.init(this, 79766);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (UpdateChecker.isInitialized()) {
                    UpdateChecker updateChecker = UpdateChecker.get();
                    updateChecker.requestUpdateCheck().thenAccept(result -> {
                        if (result.requiresUpdate()) {
                            getLogger().warning("This server is still running " + getDescription().getVersion() + " of Superheroes");
                            getLogger().warning("The latest version is " + result.getNewestVersion());
                            getLogger().warning("Updating is important to ensure there are not any bugs or vulnerabilities.");
                            getLogger().warning("As well as ensuring your players have the best time when using the superheroes!");
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.hasPermission("superheroes.notify")) {
                                    player.sendMessage("This server is still running " + getDescription().getVersion() + " of Superheroes");
                                    player.sendMessage("The latest version is " + result.getNewestVersion());
                                    player.sendMessage("Updating is important to ensure there are not any bugs or vulnerabilities.");
                                    player.sendMessage("As well as ensuring your players have the best time when using the superheroes!");
                                }
                            }
                        }
                    });
                }
            }
        }.runTaskTimer(this, 6000L, 432000L);
    }

    public void handleMetrics() {
        Metrics metrics = new Metrics(this, 8671);
        if (!metrics.isEnabled()) {
            getLogger().log(Level.WARNING, "[Superheroes] You have disabled bstats, this is very sad :(");
        }
        metrics.addCustomChart(new Metrics.AdvancedPie("players_using_each_superhero", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String powerName = heroHandler.getSuperhero(player).getName();
                int currentCount = valueMap.getOrDefault(powerName, 0);
                currentCount++;
                valueMap.put(powerName, currentCount);
            }
            return valueMap;
        }));
        metrics.addCustomChart(new Metrics.AdvancedPie("players_using_each_skill", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                Collection<Integer> skills = heroHandler.getSuperhero(player).getSkills();
                for (int skill : skills) {
                    int currentCount = valueMap.getOrDefault(Skill.getName(skill), 0);
                    currentCount++;
                    valueMap.put(Skill.getName(skill), currentCount);
                }
            }
            return valueMap;
        }));
        metrics.addCustomChart(new Metrics.SimplePie("superheroes_plus_ultra_usage", () -> Bukkit.getPluginManager().getPlugin("SuperheroesPlusUltra") != null ? "Yes" : "No"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        bukkitAudiences.close();
        superheroes.getHeroHandler().getHeroIOHandler().shutdown();
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public HeroHandler getHeroHandler() {
        return heroHandler;
    }

    public static Superheroes getInstance() { return superheroes; }

    public boolean hasSkillsLibrary() {
        return hasSkillsLibrary;
    }

    private void handleAliases(HeroCommand heroCommand, PluginCommand command) {
        List<String> commandAliases = configHandler.getCommandAliases();
        if (commandAliases.size() > 0) {
            BukkitCommand aliasCommand = new BukkitCommand(commandAliases.get(0)) {
                @Override
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
            try {
                Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
                commandMap.register("superheroes", aliasCommand);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
