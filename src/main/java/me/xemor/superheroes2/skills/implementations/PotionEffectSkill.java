package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.PotionEffectSkillData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class PotionEffectSkill extends SkillImplementation {

    public PotionEffectSkill(HeroHandler heroHandler) {
        super(heroHandler);
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
                PotionEffectSkillData potionEffectSkillData = (PotionEffectSkillData) skillData;
                player.addPotionEffect(potionEffectSkillData.getPotionEffect());
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
                PotionEffectType type = PotionEffectType.getByName(skillData.getData().getString("type").toUpperCase());
                e.getPlayer().removePotionEffect(type);
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
