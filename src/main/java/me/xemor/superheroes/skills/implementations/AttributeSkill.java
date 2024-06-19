package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.events.SuperheroesReloadEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.AttributeSkillData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class AttributeSkill extends SkillImplementation {
    public AttributeSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onChange(PlayerChangedSuperheroEvent e) {
        // Loss
        Collection<SkillData> oldSkillData = e.getOldHero().getSkillData(Skill.getSkill("ATTRIBUTE"));
        for (SkillData data : oldSkillData) {
            if (data instanceof AttributeSkillData attributeSkillData) {
                attributeSkillData.getAttributeData().resetAttributes(e.getPlayer());
            }
        }

        // Gain
        Collection<SkillData> newSkillData = e.getNewHero().getSkillData(Skill.getSkill("ATTRIBUTE"));
        for (SkillData data : newSkillData) {
            if (data instanceof AttributeSkillData attributeSkillData) {
                attributeSkillData.getAttributeData().resetAttributes(e.getPlayer());
                attributeSkillData.getAttributeData().applyAttributes(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onReload(SuperheroesReloadEvent e) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Superhero superhero = Superheroes.getInstance().getHeroHandler().getSuperhero(player);
            Collection<SkillData> oldSkillData = superhero.getSkillData(Skill.getSkill("ATTRIBUTE"));
            for (SkillData data : oldSkillData) {
                if (data instanceof AttributeSkillData attributeSkillData) {
                    attributeSkillData.getAttributeData().resetAttributes(player);
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Superheroes.getScheduling().entitySpecificScheduler(player).runDelayed((task) -> {
                Superhero superhero = Superheroes.getInstance().getHeroHandler().getSuperhero(player);
                Collection<SkillData> oldSkillData = superhero.getSkillData(Skill.getSkill("ATTRIBUTE"));
                for (SkillData data : oldSkillData) {
                    if (data instanceof AttributeSkillData attributeSkillData) {
                        attributeSkillData.getAttributeData().applyAttributes(player);
                    }
                }
            }, () -> {}, 1L);
        }
    }
}
