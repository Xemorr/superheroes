package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.events.SuperheroesReloadEvent;
import me.xemor.superheroes.skills.skilldata.AttributeSkillData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Collection;

public class AttributeSkill extends SkillImplementation {
    public AttributeSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onChange(PlayerChangedSuperheroEvent e) {
        // Loss
        Superhero superhero = e.getOldHero();
        Collection<SkillData> oldSkillData = superhero.getSkillData("ATTRIBUTE");
        for (SkillData data : oldSkillData) {
            if (data instanceof AttributeSkillData attributeSkillData) {
                attributeSkillData.getAttributes().resetAttributes(e.getPlayer());
            }
        }

        // Gain
        superhero = e.getNewHero();
        Collection<SkillData> newSkillData = superhero.getSkillData("ATTRIBUTE");
        for (SkillData data : newSkillData) {
            if (data instanceof AttributeSkillData attributeSkillData) {
                attributeSkillData.getAttributes().resetAttributes(e.getPlayer());
                attributeSkillData.getAttributes().applyAttributes(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onReload(SuperheroesReloadEvent e) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Superhero superhero = Superheroes.getInstance().getHeroHandler().getSuperhero(player);
            Collection<SkillData> oldSkillData = superhero.getSkillData("ATTRIBUTE");
            for (SkillData data : oldSkillData) {
                if (data instanceof AttributeSkillData attributeSkillData) {
                    attributeSkillData.getAttributes().resetAttributes(player);
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Superheroes.getScheduling().entitySpecificScheduler(player).runDelayed((task) -> {
                Superhero superhero = Superheroes.getInstance().getHeroHandler().getSuperhero(player);
                Collection<SkillData> oldSkillData = superhero.getSkillData("ATTRIBUTE");
                for (SkillData data : oldSkillData) {
                    if (data instanceof AttributeSkillData attributeSkillData) {
                        attributeSkillData.getAttributes().applyAttributes(player);
                    }
                }
            }, () -> {}, 1L);
        }
    }
}
