package me.xemor.superheroes.skills.implementations;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class EggLayerSkill extends SkillImplementation {

    public final Multimap<UUID, EggLayerRunnable> map = Multimaps.synchronizedSetMultimap(HashMultimap.create());

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
            Collection<EggLayerRunnable> eggLayerRunnables = map.get(player.getUniqueId());
            for (EggLayerRunnable eggLayerRunnable : eggLayerRunnables) {
                if (skillData == eggLayerRunnable.eggLayerData()) {
                    eggLayerRunnable.task().cancel();
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
        ScheduledTask[] taskSingleton = new ScheduledTask[1];
        ScheduledTask task = Superheroes.getScheduling().entitySpecificScheduler(player).runAtFixedRate(() -> {
            Superhero currentPower = heroHandler.getSuperhero(player);
            if (!superhero.equals(currentPower)) {
                taskSingleton[0].cancel();
                return;
            }
            if (!superhero.getSkillData(Skill.getSkill("EGGLAYER")).contains(eggLayerData)) {
                taskSingleton[0].cancel();
                return;
            }
            if (eggLayerData.areConditionsTrue(player)) {
                World world = player.getWorld();
                Location location = player.getLocation();
                world.dropItemNaturally(location, eggLayerData.getToLay());
            }
        }, () -> {}, eggLayerData.getTickDelay(), eggLayerData.getTickDelay());
        taskSingleton[0] = task;
        map.put(player.getUniqueId(), new EggLayerRunnable(eggLayerData, task));
    }

    public record EggLayerRunnable(EggLayerData eggLayerData, ScheduledTask task) {}
}
