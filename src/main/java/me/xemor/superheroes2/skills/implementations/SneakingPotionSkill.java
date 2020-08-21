package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.PotionEffectData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Collection;

public class SneakingPotionSkill extends SkillImplementation {
    public SneakingPotionSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = getPowersHandler().getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.SNEAKINGPOTION);
        for (SkillData skillData : skillDatas) {
            PotionEffectData potionEffectData = (PotionEffectData) skillData;
            if (e.isSneaking()) {
                e.getPlayer().addPotionEffect(potionEffectData.getPotionEffect());
            }
            else {
                e.getPlayer().removePotionEffect(potionEffectData.getPotionEffect().getType());
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = getPowersHandler().getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.SNEAKINGPOTION);
        for (SkillData skillData : skillDatas) {
            PotionEffectData potionEffectData = (PotionEffectData) skillData;
            e.getPlayer().removePotionEffect(potionEffectData.getPotionEffect().getType());
        }
    }
}
