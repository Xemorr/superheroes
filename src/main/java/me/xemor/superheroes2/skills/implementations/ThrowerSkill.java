package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.SkillCooldownHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.ThrowerData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ThrowerSkill extends SkillImplementation {

    SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler("");

    public ThrowerSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onThrow(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.THROWER);
        for (SkillData skillData : skillDatas) {
            ThrowerData throwerData = (ThrowerData) skillData;
            if (throwerData.getActions().contains(e.getAction())) {
                ItemStack item = e.getItem();
                if (item != null) {
                    if (item.getAmount() >= throwerData.getAmmoCost() && item.isSimilar(throwerData.getAmmo())) {
                        e.setCancelled(true);
                        if (skillCooldownHandler.isCooldownOver(throwerData, player.getUniqueId(), throwerData.getCooldownMessage())) {
                            EntityType entityType = throwerData.getEntityType();
 /*               if (Projectile.class.isAssignableFrom(entityType.getEntityClass())) { //"isAssignableFrom" ensures that the argument is an instance of the class it's being used on.
                    player.launchProjectile((Class<? extends Projectile>) entityType.getEntityClass());
                }*/
                            World world = player.getWorld();
                            Entity entity = world.spawnEntity(player.getEyeLocation(), entityType);
                            entity.setVelocity(player.getEyeLocation().getDirection().multiply(throwerData.getVelocity()));
                            if (entity instanceof AbstractArrow) {
                                AbstractArrow arrow = (AbstractArrow) entity;
                                arrow.setPickupStatus(throwerData.canPickUp());
                                arrow.setDamage(throwerData.getDamage());
                            }
                            useAmmo(throwerData.getAmmoCost(), item);
                            skillCooldownHandler.startCooldown(throwerData, throwerData.getCooldown(), player.getUniqueId());
                        }
                    }
                }
            }
        }
    }

    public void useAmmo(int cost, ItemStack item) {
        int paidFor = 0;
        if (item.getAmount() >= (cost - paidFor)) {
            item.setAmount(item.getAmount() - (cost - paidFor));
        } else {
            paidFor += item.getAmount();
            item.setType(Material.AIR);
        }
    }
}
