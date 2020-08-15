package me.xemor.superheroes2.skills;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.skilldata.PotionEffectData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class PotionEffectSkill extends SkillImplementation {

    public PotionEffectSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onPowerGained(PlayerGainedSuperheroEvent e) {
        dishoutPotionEffects(e.getPlayer());
    }

    public void dishoutPotionEffects(Player player) {
        Superhero superhero = powersHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.POTIONEFFECT);
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                PotionEffectData potionEffectData = (PotionEffectData) skillData;
                player.addPotionEffect(potionEffectData.getPotionEffect());
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                dishoutPotionEffects(e.getPlayer());
            }
        }.runTaskLater(powersHandler.getPlugin(), 5L);
    }

    @EventHandler
    public void onPowerLost(PlayerLostSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getHero().getSkillData(Skill.POTIONEFFECT);
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                PotionEffectType type = PotionEffectType.getByName(skillData.getData().getString("type").toUpperCase());
                e.getPlayer().removePotionEffect(type);
            }
        }
    }
}
