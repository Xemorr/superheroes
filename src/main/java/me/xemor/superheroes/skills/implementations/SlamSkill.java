package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.SlamData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.UUID;

public class SlamSkill extends SkillImplementation {

    final SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public SlamSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onPunch(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR) {
            Player player = e.getPlayer();
            Superhero superhero = heroHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("SLAM"));
            for (SkillData skillData : skillDatas) {
                SlamData slamData = (SlamData) skillData;
                ItemStack currentHand = player.getInventory().getItemInMainHand();
                if (currentHand.getType().equals(Material.AIR)) {
                    currentHand = new ItemStack(Material.AIR);
                }
                if (currentHand.isSimilar(slamData.getHand())) {
                    UUID uuid = player.getUniqueId();
                    if (skillCooldownHandler.isCooldownOver(slamData, uuid)) {
                        if (player.getFoodLevel() > slamData.getMinimumFood()) {
                            if (slamData.areConditionsTrue(player)) {
                                skillCooldownHandler.startCooldown(slamData, slamData.getAirCooldown(), uuid);
                                player.setFoodLevel(player.getFoodLevel() - slamData.getFoodCost());
                                doDoomfistJump(player, superhero, slamData);
                            }
                        }
                    }
                }
            }
        }
    }

    public void doDoomfistJump(Player player, Superhero superhero, SlamData slamData) {
        player.setVelocity(player.getEyeLocation().getDirection().multiply(1.8).add(new Vector(0, 0.5, 0)));
        Superheroes.getScheduling().entitySpecificScheduler(player).runAtFixedRate((task) -> {
            Block under = player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0));
            if (!heroHandler.getSuperhero(player).equals(superhero)) {
                task.cancel();
                return;
            }
            if (!under.getType().isAir()) {
                World world = player.getWorld();
                Collection<Entity> entities = world.getNearbyEntities(player.getLocation(), slamData.getDiameterRadius(), 3, slamData.getDiameterRadius(), entity -> entity instanceof LivingEntity);
                for (Entity entity : entities) {
                    if (!player.equals(entity)) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        if (slamData.getDamage() > 0) {
                            livingEntity.damage(slamData.getDamage(), player);
                        }
                    }
                }
                skillCooldownHandler.startCooldown(slamData, player.getUniqueId());
                task.cancel();
            }
        }, () -> {}, 6L, 2L);
    }
}
