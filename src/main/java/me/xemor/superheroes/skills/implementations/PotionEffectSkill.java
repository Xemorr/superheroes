package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
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
                givePotionEffects(player, heroHandler.getSuperhero(player));
           }
        }, 0, 10);
    }

    @EventHandler
    public void onPowerGained(PlayerChangedSuperheroEvent e) {
        givePotionEffects(e.getPlayer(), e.getNewHero());
    }

    public void givePotionEffects(Player player, Superhero superhero) {
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
                givePotionEffects(e.getPlayer(), Superheroes.getInstance().getHeroHandler().getSuperhero(e.getPlayer()));
            }
        }.runTaskLater(heroHandler.getPlugin(), 5L);
    }

    @EventHandler
    public void onPowerLost(PlayerChangedSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getOldHero().getSkillData(Skill.getSkill("POTIONEFFECT"));
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
                    givePotionEffects(e.getPlayer(), Superheroes.getInstance().getHeroHandler().getSuperhero(e.getPlayer()));
                }
            }.runTaskLater(heroHandler.getPlugin(), 3L);
        }
    }
}
