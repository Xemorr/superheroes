package me.xemor.superheroes.skills.implementations;

import me.xemor.foliahacks.PlayerPostRespawnFoliaEvent;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.AttributeSkillData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class FlightSkill extends SkillImplementation {
    public FlightSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Superheroes.getScheduling().entitySpecificScheduler(e.getPlayer()).runDelayed(() -> {
            Superhero superhero = Superheroes.getInstance().getHeroHandler().getSuperhero(e.getPlayer());
            Collection<SkillData> newSkillData = superhero.getSkillData(Skill.getSkill("FLIGHT"));
            for (SkillData data : newSkillData) {
                e.getPlayer().setAllowFlight(true);
                e.getPlayer().setFlying(true);
            }
        }, () -> {}, 10L);
    }

    @EventHandler
    public void onRespawn(PlayerPostRespawnFoliaEvent e) {
        Superheroes.getScheduling().entitySpecificScheduler(e.getPlayer()).runDelayed(() -> {
            Superhero superhero = Superheroes.getInstance().getHeroHandler().getSuperhero(e.getPlayer());
            Collection<SkillData> newSkillData = superhero.getSkillData(Skill.getSkill("FLIGHT"));
            for (SkillData data : newSkillData) {
                e.getPlayer().setAllowFlight(true);
                e.getPlayer().setFlying(true);
            }
        }, () -> {}, 1L);
    }

    @EventHandler
    public void onChange(PlayerChangedSuperheroEvent e) {
        // Loss
        Collection<SkillData> oldSkillData = e.getOldHero().getSkillData(Skill.getSkill("FLIGHT"));
        for (SkillData data : oldSkillData) {
            e.getPlayer().setAllowFlight(false);
            e.getPlayer().setFlying(false);
        }

        // Gain
        Collection<SkillData> newSkillData = e.getNewHero().getSkillData(Skill.getSkill("FLIGHT"));
        for (SkillData data : newSkillData) {
            e.getPlayer().setAllowFlight(true);
        }
    }
}
