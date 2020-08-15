package me.xemor.superheroes2;

import me.xemor.superheroes2.commands.HeroCMD;
import me.xemor.superheroes2.skills.*;
import me.xemor.superheroes2.skills.skilldata.InstantBreakData;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Superheroes2 extends JavaPlugin {

    ConfigHandler configHandler;
    PowersHandler powersHandler;
    SkillImplementation[] skills;

    @Override
    public void onEnable() {
        // Plugin startup logic
        powersHandler = new PowersHandler(this);
        configHandler = new ConfigHandler(this, powersHandler);
        powersHandler.setConfigHandler(configHandler);
        skills = new SkillImplementation[]{
                new PotionEffectSkill(powersHandler),
                new InstantBreak(powersHandler),
                new LightSkill(powersHandler),
                new NoHungerSkill(powersHandler),
                new ElectrifiedSkill(powersHandler),
                new SlimeSkill(powersHandler)
        };
        for (SkillImplementation skill : skills) {
            this.getServer().getPluginManager().registerEvents(skill, this);
        }
        this.getServer().getPluginManager().registerEvents(new JoinListener(powersHandler), this);
        HeroCMD heroCMD = new HeroCMD(powersHandler);
        PluginCommand command = this.getCommand("hero");
        command.setExecutor(heroCMD);
        command.setTabCompleter(heroCMD);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
