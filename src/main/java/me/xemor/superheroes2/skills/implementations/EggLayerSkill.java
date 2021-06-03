package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.EggLayerData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class EggLayerSkill extends SkillImplementation {
    public EggLayerSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onPowerGain(PlayerGainedSuperheroEvent e) {
        startRunnable(e.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        startRunnable(e.getPlayer());
    }

    public void startRunnable(Player player) {
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("EGGLAYER"));
        for (SkillData skillData : skillDatas) {
            EggLayerData eggLayerData = (EggLayerData) skillData;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Superhero currentPower = heroHandler.getSuperhero(player);
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }
                    if (!superhero.equals(currentPower)) {
                        cancel();
                        return;
                    }
                    World world = player.getWorld();
                    Location location = player.getLocation();
                    world.dropItemNaturally(location, eggLayerData.getToLay());
                }
            }.runTaskTimer(heroHandler.getPlugin(), 0L, eggLayerData.getTickDelay());
        }

    }
}
