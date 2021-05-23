package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.PotionEffectSkillData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
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
            PotionEffectSkillData potionEffectSkillData = (PotionEffectSkillData) skillData;
            if (e.isSneaking()) {
                e.getPlayer().addPotionEffect(potionEffectSkillData.getPotionEffect());
            }
            else {
                e.getPlayer().removePotionEffect(potionEffectSkillData.getPotionEffect().getType());
            }
        }
    }

    @EventHandler
    public void onPowerLost(PlayerLostSuperheroEvent e) {
        Collection<SkillData> skillDatas = e.getHero().getSkillData(Skill.getSkill("SNEAKINGPOTION"));
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                PotionEffectSkillData sneakingPotionSkill = (PotionEffectSkillData) skillData;
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
