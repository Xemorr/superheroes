package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.PotionEffectSkillData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.SneakingPotionData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
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
                    e.getPlayer().addPotionEffect(sneakingPotionData.getPotionEffect());
                }
            }
            else {
                e.getPlayer().removePotionEffect(sneakingPotionData.getPotionEffect().getType());
            }
        }
    }

    @EventHandler
    public void onPowerLost(PlayerLostSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getHero().getSkillData(Skill.getSkill("SNEAKINGPOTION"));
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                SneakingPotionData sneakingPotionSkill = (SneakingPotionData) skillData;
                PotionEffectType type = sneakingPotionSkill.getPotionEffect().getType();
                e.getPlayer().removePotionEffect(type);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = getPowersHandler().getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("SNEAKINGPOTION"));
        for (SkillData skillData : skillDatas) {
            PotionEffectSkillData potionEffectSkillData = (PotionEffectSkillData) skillData;
            e.getPlayer().removePotionEffect(potionEffectSkillData.getPotionEffect().getType());
        }
    }
}
