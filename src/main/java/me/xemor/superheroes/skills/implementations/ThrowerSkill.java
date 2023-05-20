package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.ThrowerData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ThrowerSkill extends SkillImplementation {

    SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public ThrowerSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onThrow(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("THROWER"));
        for (SkillData skillData : skillDatas) {
            ThrowerData throwerData = (ThrowerData) skillData;
            if (throwerData.getActions().contains(e.getAction())) {
                ItemStack item = e.getItem();
                if (item != null) {
                    if (item.getAmount() >= throwerData.getAmmoCost() && throwerData.getAmmo().matches(item)) {
                        if (skillCooldownHandler.isCooldownOver(throwerData, player.getUniqueId())) {
                            if (skillData.areConditionsTrue(player)) {
                                e.setCancelled(true);
                                EntityType entityType = throwerData.getEntityType();
                                World world = player.getWorld();
                                Entity entity = world.spawnEntity(player.getEyeLocation(), entityType);
                                entity.setVelocity(player.getEyeLocation().getDirection().multiply(throwerData.getVelocity()));
                                if (entity instanceof AbstractArrow arrow) {
                                    arrow.setPickupStatus(throwerData.canPickUp());
                                    arrow.setDamage(throwerData.getDamage());
                                }
                                if (entity instanceof Projectile projectile) {
                                    projectile.setShooter(player);
                                }
                                useAmmo(throwerData.getAmmoCost(), item);
                                skillCooldownHandler.startCooldown(throwerData, throwerData.getCooldown(), player.getUniqueId());
                            }
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
