package me.xemor.superheroes2.skills.implementations;

import de.themoep.minedown.MineDown;
import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.SkillCooldownHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.EraserData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.Collection;

public class EraserSkill extends SkillImplementation {
    public EraserSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    Superhero erased = new Superhero("ERASED", ChatColor.translateAlternateColorCodes('&', "&7&lERASED"), "Their power has been erased");
    SkillCooldownHandler skillCooldownHandler =new SkillCooldownHandler();

    @EventHandler
    public void onSight(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            Superhero superhero = heroHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData(Skill.ERASER);
            for (SkillData skillData : skillDatas) {
                EraserData eraserData = (EraserData) skillData;
                if (skillCooldownHandler.isCooldownOver(eraserData, player.getUniqueId())) {
                    World world = player.getWorld();
                    Location eyeLocation = player.getEyeLocation();
                    eyeLocation = eyeLocation.clone().add(eyeLocation.getDirection());
                    RayTraceResult rayTraceResult = world.rayTrace(eyeLocation, eyeLocation.getDirection(), eraserData.getRange(), FluidCollisionMode.NEVER, true, 1.0,
                            entity -> entity instanceof Player && !entity.equals(player));
                    if (rayTraceResult == null) {
                        continue;
                    }
                    Entity entity = rayTraceResult.getHitEntity();
                    if (entity != null) {
                        Player hitPlayer = (Player) entity;
                        temporarilyRemoveHero(hitPlayer, player, eraserData);
                        skillCooldownHandler.startCooldown(eraserData, eraserData.getCooldown(), player.getUniqueId());
                    }
                }
            }
        }
    }

    public void temporarilyRemoveHero(Player player, Player remover, EraserData eraserData) {
        final Superhero oldPower = heroHandler.getSuperhero(player);
        heroHandler.setHeroInMemory(player, erased);
        BaseComponent[] removedMessage = new MineDown(eraserData.getRemovedMessage()).replace("player", player.getDisplayName()).toComponent();
        if (remover != null) {
            remover.spigot().sendMessage(removedMessage);
        }
        player.spigot().sendMessage(removedMessage);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (heroHandler.getSuperhero(player) == erased) {
                    heroHandler.setHeroInMemory(player, oldPower);
                    BaseComponent[] returnedMessage = new MineDown(eraserData.getReturnedMessage()).replace("player", player.getDisplayName()).toComponent();
                    if (remover != null) {
                        remover.spigot().sendMessage(returnedMessage);
                    }
                    player.spigot().sendMessage(returnedMessage);
                }
            }
        }.runTaskLater(heroHandler.getPlugin(), eraserData.getDuration());
    }
}
