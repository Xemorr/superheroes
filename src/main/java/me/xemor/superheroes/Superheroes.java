package me.xemor.superheroes;

import me.xemor.superheroes.Commands.PowerCommand;
import me.xemor.superheroes.Commands.Reroll;
import me.xemor.superheroes.Superpowers.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class Superheroes extends JavaPlugin {

    PowersHandler powersHandler = new PowersHandler(this);
    Superpower[] superpowers = new Superpower[]{new Pickpocket(powersHandler), new CreeperPower(powersHandler), new Strongman(powersHandler), new Eraserhead(powersHandler), new Enderman(powersHandler), new Snowman(powersHandler), new Repulsion(powersHandler), new Frozone(powersHandler), new LavaWalker(powersHandler), new NoFire(powersHandler), new Pyromaniac(powersHandler), new Gun(powersHandler), new Trap(powersHandler), new Phase(powersHandler), new KingMidas(powersHandler), new Mole(powersHandler), new Aerosurfer(powersHandler), new GravityGuy(powersHandler), new Floral(powersHandler), new ExtraHeartMan(powersHandler)};

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
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
        for (Superpower superpower : superpowers) {
            this.getServer().getPluginManager().registerEvents(superpower, this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
