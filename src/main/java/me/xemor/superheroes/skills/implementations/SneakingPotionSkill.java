package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.SneakingPotionData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class SneakingPotionSkill extends SkillImplementation {
    public SneakingPotionSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = getPowersHandler().getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("SNEAKINGPOTION"));
        for (SkillData skillData : skillDatas) {
            SneakingPotionData sneakingPotionData = (SneakingPotionData) skillData;
            if (e.isSneaking()) {
                if (skillData.areConditionsTrue(player)) {
                    sneakingPotionData.getPotionEffect().ifPresent(e.getPlayer()::addPotionEffect);
                }
            }
            else {
                sneakingPotionData.getPotionEffect().ifPresent(potionEffect -> e.getPlayer().removePotionEffect(potionEffect.getType()));
            }
        }
    }

    @EventHandler
    public void onPowerLost(PlayerChangedSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getOldHero().getSkillData(Skill.getSkill("SNEAKINGPOTION"));
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                SneakingPotionData sneakingPotionSkill = (SneakingPotionData) skillData;
                sneakingPotionSkill.getPotionEffect().map(PotionEffect::getType).ifPresent(e.getPlayer()::removePotionEffect);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = getPowersHandler().getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("SNEAKINGPOTION"));
        for (SkillData skillData : skillDatas) {
            SneakingPotionData sneakingPotionData = (SneakingPotionData) skillData;
            sneakingPotionData.getPotionEffect().map(PotionEffect::getType).ifPresent(e.getPlayer()::removePotionEffect);
        }
    }
}
