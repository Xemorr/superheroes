package me.xemor.superheroes.skills.implementations;

import me.xemor.foliahacks.PlayerPostRespawnFoliaEvent;
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
        Superheroes.getScheduling().globalRegionalScheduler().runAtFixedRate(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Superheroes.getScheduling().entitySpecificScheduler(player).run(() -> givePotionEffects(player, heroHandler.getSuperhero(player)), () -> {});
            }
        }, 1L, 20L);
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
                    potionEffectSkillData.getPotionEffect().ifPresent(effectToApply -> {
                        if (effectToApply.getType().equals(PotionEffectType.HEALTH_BOOST)) {
                            PotionEffect potionEffect = player.getPotionEffect(effectToApply.getType());
                            if (potionEffect != null && potionEffect.getDuration() > 2) {
                                return;
                            }
                        }
                        player.addPotionEffect(effectToApply);
                    });
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerPostRespawnFoliaEvent e) {
        Superheroes.getScheduling().entitySpecificScheduler(e.getPlayer()).runDelayed(() -> {
            givePotionEffects(e.getPlayer(), Superheroes.getInstance().getHeroHandler().getSuperhero(e.getPlayer()));
        }, () -> {}, 5L);
    }

    @EventHandler
    public void onPowerLost(PlayerChangedSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getOldHero().getSkillData(Skill.getSkill("POTIONEFFECT"));
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                if (skillData instanceof PotionEffectSkillData potionEffectData) {
                    potionEffectData.getPotionEffect().map(PotionEffect::getType).ifPresent(e.getPlayer()::removePotionEffect);
                }
            }
        }
    }

    @EventHandler
    public void onMilkDrink(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.MILK_BUCKET) {
            Superheroes.getScheduling().entitySpecificScheduler(e.getPlayer()).runDelayed(() -> {
                givePotionEffects(e.getPlayer(), Superheroes.getInstance().getHeroHandler().getSuperhero(e.getPlayer()));
            }, () -> {}, 3L);
        }
    }
}
