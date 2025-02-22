package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.RemoteDetonationData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.RayTraceResult;

import java.util.Collection;
import java.util.List;

public class RemoteDetonationSkill extends SkillImplementation {

    final SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public RemoteDetonationSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("REMOTEDETONATION"));
            for (SkillData skillData : skillDatas) {
                RemoteDetonationData remoteDetonationData = (RemoteDetonationData) skillData;
                if (skillCooldownHandler.isCooldownOver(remoteDetonationData, player.getUniqueId())) {
                    Entity entity = raytrace(player, remoteDetonationData.getExplodable());
                    if (entity == null) return;
                    skillData.ifConditionsTrue(() -> {
                        Location location = entity.getLocation();
                        if (!(entity instanceof Player) && remoteDetonationData.removeDetonatedEntity()) {
                            if (entity instanceof LivingEntity) {
                                ((LivingEntity) entity).setHealth(0);
                            }
                            else {
                                entity.remove();
                            }
                        }
                        World world = player.getWorld();
                        world.createExplosion(location, remoteDetonationData.getExplosionStrength(), remoteDetonationData.spawnsFire(), remoteDetonationData.breakBlocks(), entity);
                        skillCooldownHandler.startCooldown(remoteDetonationData, player.getUniqueId());
                    }, player, entity);
                }
            }
        }
    }

    public Entity raytrace(Player player, List<EntityType> explodable) {
        World world = player.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 32, 0.5, (entity) -> entity != player && explodable.contains(entity.getType()));
        if (rayTraceResult == null) {
            return null;
        }
        return rayTraceResult.getHitEntity();
    }
}
