package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.LightSkillData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class LightSkill extends SkillImplementation {
    public LightSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onGain(PlayerChangedSuperheroEvent e) {
        Superhero superhero = e.getNewHero();
        if (superhero.hasSkill(Skill.getSkill("LIGHT"))) {
            runnable(e.getPlayer(), superhero);
        }
    }

    @EventHandler
    public void onLost(PlayerChangedSuperheroEvent e) {
        Superhero superhero = e.getOldHero();
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("LIGHT"));
        for (SkillData skillData : skillDatas) {
            LightSkillData lightData = (LightSkillData) skillData;
            lightData.getPotionEffect().ifPresent((potionEffect -> e.getPlayer().removePotionEffect(potionEffect.getType())));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Superhero superhero = heroHandler.getSuperhero(e.getPlayer());
        if (superhero.hasSkill(Skill.getSkill("LIGHT"))) {
            runnable(e.getPlayer(), superhero);
        }
    }

    public void runnable(Player player, Superhero superhero) {
        Superheroes.getScheduling().entitySpecificScheduler(player).runAtFixedRate(task -> {
            Collection<SkillData> data = superhero.getSkillData(Skill.getSkill("LIGHT"));
            if (data.isEmpty()) {
                task.cancel();
                return;
            }
            if (heroHandler.getSuperhero(player) != superhero) {
                task.cancel();
                return;
            }
            for (SkillData skillData : data) {
                LightSkillData lightData = (LightSkillData) skillData;
                if (player.getWorld().getBlockAt(player.getLocation()).getLightLevel() > 10) {
                    if (lightData.areConditionsTrue(player)) {
                        lightData.getPotionEffect().ifPresent(player::addPotionEffect);
                    }
                } else {
                    lightData.getPotionEffect().map(PotionEffect::getType).ifPresent(player::removePotionEffect);
                }
            }
        }, () -> {}, 1L, 20L);
    }


}
