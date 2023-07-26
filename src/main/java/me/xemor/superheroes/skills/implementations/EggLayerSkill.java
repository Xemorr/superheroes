package me.xemor.superheroes.skills.implementations;

import com.google.common.collect.HashMultimap;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.EggLayerData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class EggLayerSkill extends SkillImplementation {

    public final HashMultimap<UUID, EggLayerRunnable> map = HashMultimap.create();

    public EggLayerSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onPowerGain(PlayerChangedSuperheroEvent e) {
        initialiseSkill(e.getPlayer(), e.getNewHero());
    }

    @EventHandler
    public void onPowerLoss(PlayerChangedSuperheroEvent e) {
        removeSkill(e.getPlayer(), e.getOldHero());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        initialiseSkill(e.getPlayer(), Superheroes.getInstance().getHeroHandler().getSuperhero(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        removeSkill(e.getPlayer(), Superheroes.getInstance().getHeroHandler().getSuperhero(e.getPlayer()));
    }

    public void removeSkill(Player player, Superhero oldHero) {
        Collection<SkillData> skillDatas = oldHero.getSkillData(Skill.getSkill("EGGLAYER"));
        for (SkillData skillData : skillDatas) {
            Set<EggLayerRunnable> eggLayerRunnables = map.get(player.getUniqueId());
            for (EggLayerRunnable eggLayerRunnable : eggLayerRunnables) {
                if (skillData == eggLayerRunnable.eggLayerData()) {
                    eggLayerRunnable.bukkitRunnable().cancel();
                }
            }
        }
    }

    public void initialiseSkill(Player player, Superhero superhero) {
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("EGGLAYER"));
        for (SkillData skillData : skillDatas) {
            EggLayerData eggLayerData = (EggLayerData) skillData;
            startRunnable(player, eggLayerData, superhero);
        }
    }

    public void startRunnable(Player player, EggLayerData eggLayerData, Superhero superhero) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Superhero currentPower = heroHandler.getSuperhero(player);
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                if (!superhero.equals(currentPower)) {
                    cancel();
                    return;
                }
                if (!superhero.getSkillData(Skill.getSkill("EGGLAYER")).contains(eggLayerData)) {
                    cancel();
                    return;
                }
                if (eggLayerData.areConditionsTrue(player)) {
                    World world = player.getWorld();
                    Location location = player.getLocation();
                    world.dropItemNaturally(location, eggLayerData.getToLay());
                }
            }
        };
        runnable.runTaskTimer(heroHandler.getPlugin(), eggLayerData.getTickDelay(), eggLayerData.getTickDelay());
        map.put(player.getUniqueId(), new EggLayerRunnable(eggLayerData, runnable));
    }

    public record EggLayerRunnable(EggLayerData eggLayerData, BukkitRunnable bukkitRunnable) {}
}
