package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.PotionEffectSkillData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class PotionEffectSkill extends SkillImplementation {

    public PotionEffectSkill(HeroHandler heroHandler) {
        super(heroHandler);
        Bukkit.getScheduler().runTaskTimer(heroHandler.getPlugin(), () -> {
           for (Player player : Bukkit.getOnlinePlayers()) {
                givePotionEffects(player);
           }
        }, 0, 10);
    }

    @EventHandler
    public void onPowerGained(PlayerGainedSuperheroEvent e) {
        givePotionEffects(e.getPlayer());
    }

    public void givePotionEffects(Player player) {
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("POTIONEFFECT"));
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                if (skillData.areConditionsTrue(player)) {
                    PotionEffectSkillData potionEffectSkillData = (PotionEffectSkillData) skillData;
                    PotionEffect effectToApply = potionEffectSkillData.getPotionEffect();
                    if (effectToApply.getType().equals(PotionEffectType.HEALTH_BOOST)) {
                        PotionEffect potionEffect = player.getPotionEffect(effectToApply.getType());
                        if (potionEffect != null && potionEffect.getDuration() > 2) {
                            continue;
                        }
                    }
                    player.addPotionEffect(potionEffectSkillData.getPotionEffect());
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                givePotionEffects(e.getPlayer());
            }
        }.runTaskLater(heroHandler.getPlugin(), 5L);
    }

    @EventHandler
    public void onPowerLost(PlayerLostSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getHero().getSkillData(Skill.getSkill("POTIONEFFECT"));
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                if (skillData instanceof PotionEffectSkillData potionEffectData) {
                    e.getPlayer().removePotionEffect(potionEffectData.getPotionEffect().getType());
                }
            }
        }
    }

    @EventHandler
    public void onMilkDrink(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.MILK_BUCKET) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    givePotionEffects(e.getPlayer());
                }
            }.runTaskLater(heroHandler.getPlugin(), 3L);
        }
    }
}
