package me.xemor.superheroes2;

import me.xemor.superheroes2.commands.HeroCMD;
import me.xemor.superheroes2.commands.Reload;
import me.xemor.superheroes2.commands.Reroll;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.implementations.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class Superheroes2 extends JavaPlugin {

    ConfigHandler configHandler;
    HeroHandler heroHandler;
    SkillImplementation[] skills;

    @Override
    public void onEnable() {
        // Plugin startup logic
        configHandler = new ConfigHandler(this);
        heroHandler = new HeroHandler(this, configHandler);
        configHandler.loadSuperheroes(heroHandler);
        registerSkills();
        Reroll reroll = new Reroll(heroHandler, configHandler);
        Reload reload = new Reload(heroHandler, configHandler);
        this.getCommand("heroreload").setExecutor(reload);
        this.getServer().getPluginManager().registerEvents(reroll, this);
        this.getServer().getPluginManager().registerEvents(heroHandler, this);
        HeroCMD heroCMD = new HeroCMD(heroHandler, configHandler);
        PluginCommand command = this.getCommand("hero");
        command.setExecutor(heroCMD);
        command.setTabCompleter(heroCMD);
        this.getCommand("reroll").setExecutor(reroll);
        handleMetrics();
        checkForNewUpdate();
    }

    public void registerSkills() {
        skills = new SkillImplementation[]{
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
                new ConvertDropsSkill(heroHandler)
        };
        for (SkillImplementation skill : skills) {
            this.getServer().getPluginManager().registerEvents(skill, this);
        }
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
                        }
                    });
                }
            }
        }.runTaskTimer(this, 0L, 432000L);
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
                Collection<Skill> skills = heroHandler.getSuperhero(player).getSkills();
                for (Skill skill : skills) {
                    int currentCount = valueMap.getOrDefault(skill.name(), 0);
                    currentCount++;
                    valueMap.put(skill.name(), currentCount);
                }
            }
            return valueMap;
        }));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
