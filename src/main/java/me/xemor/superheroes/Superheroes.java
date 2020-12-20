package me.xemor.superheroes;

import me.xemor.superheroes.Commands.PowerCommand;
import me.xemor.superheroes.Commands.Reroll;
import me.xemor.superheroes.Superpowers.*;
import me.xemor.superheroes.Superpowers.Sorcerer.Sorcerer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Superheroes extends JavaPlugin {

    PowersHandler powersHandler = new PowersHandler(this);
    RecipeHandler recipeHandler = new RecipeHandler(powersHandler);
    Superpower[] superpowers;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        superpowers = new Superpower[]{new Pickpocket(powersHandler), new Sorcerer(powersHandler, recipeHandler, this), new Scavenger(powersHandler, recipeHandler, this), new Zeus(powersHandler), new Slime(powersHandler), new Doomfist(powersHandler, this), new Speleologist(powersHandler), new Robot(powersHandler, this), new CreeperPower(powersHandler), new Strongman(powersHandler), new Eraserhead(powersHandler), new Enderman(powersHandler), new Snowman(powersHandler), new Repulsion(powersHandler), new Frozone(powersHandler), new LavaWalker(powersHandler, this, recipeHandler), new NoFire(powersHandler), new Pyromaniac(powersHandler), new Gun(powersHandler), new Trap(powersHandler, this), new Phase(powersHandler), new KingMidas(powersHandler), new Mole(powersHandler), new Aerosurfer(powersHandler, this), new GravityGuy(powersHandler), new Floral(powersHandler), new ExtraHeartMan(powersHandler)};
        PotionEffectPowers potionEffectPowers = new PotionEffectPowers(powersHandler);
        potionEffectPowers.runTaskTimer(this, 100L, 50L);
        Chicken chicken = new Chicken(powersHandler);
        chicken.runTaskTimer(this, 100L, 40L);
        MoleRunnable moleRunnable = new MoleRunnable(powersHandler);
        moleRunnable.runTaskTimer(this, 100L, 50L);
        DiamondBlockReroll diamondBlockReroll = new DiamondBlockReroll(powersHandler);
        this.getServer().getPluginManager().registerEvents(diamondBlockReroll, this);
        JoinListener joinListener = new JoinListener(powersHandler);
        this.getServer().getPluginManager().registerEvents(joinListener, this);
        Reroll reroll = new Reroll(powersHandler);
        this.getCommand("reroll").setExecutor(reroll);
        PowerCommand powerCommand = new PowerCommand(powersHandler);
        this.getCommand("power").setExecutor(powerCommand);
        this.getServer().getPluginManager().registerEvents(recipeHandler, this);
        for (Superpower superpower : superpowers) {
            this.getServer().getPluginManager().registerEvents(superpower, this);
        }
        Metrics metrics = new Metrics(this, 8671);
        if (!metrics.isEnabled()) {
            Bukkit.getLogger().log(Level.WARNING, "[Superheroes] You have disabled bstats, this is very sad :(");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
