package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.CooldownHandler;
import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.EraserData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.RayTraceResult;

import java.util.Collection;

public class EraserSkill extends SkillImplementation {
    public EraserSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    CooldownHandler cooldownHandler = new CooldownHandler("");

    @EventHandler
    public void onSight(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            Superhero superhero = powersHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData(Skill.ERASER);
            for (SkillData skillData : skillDatas) {
                EraserData eraserData = (EraserData) skillData;
                if (cooldownHandler.isCooldownOver(skillData, player.getUniqueId())) {
                    World world = player.getWorld();
                    Location eyeLocation = player.getEyeLocation();
                    eyeLocation = eyeLocation.clone().add(eyeLocation.getDirection());
                    RayTraceResult rayTraceResult = world.rayTrace(eyeLocation, eyeLocation.getDirection(), eraserData.getRange(), FluidCollisionMode.NEVER, true, 1.0, entity -> entity instanceof Player && entity != player);
                    Entity entity = rayTraceResult.getHitEntity();
                    if (entity != null) {
                        Player hitPlayer = (Player) entity;
                        powersHandler.temporarilyRemovePower(hitPlayer, player, eraserData.getDuration());
                        cooldownHandler.startCooldown(skillData, eraserData.getCooldown(), player.getUniqueId());
                    }
                }
            }
        }
    }
}
