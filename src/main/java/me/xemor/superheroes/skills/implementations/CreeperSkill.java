package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.CreeperData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class CreeperSkill extends SkillImplementation {

    final SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public CreeperSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            Superhero superhero = heroHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("CREEPER"));
            for (SkillData skillData : skillDatas) {
                final CreeperData creeperData = (CreeperData) skillData;
                if (isOnGround(player)) {
                    final int[] timer = {0};
                    World world = player.getWorld();
                    if (skillCooldownHandler.isCooldownOver(creeperData, player.getUniqueId())) {
                        if (!creeperData.areConditionsTrue(player)) {
                            return;
                        }
                        world.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0F, 1.0F);
                        Superheroes.getScheduling().entitySpecificScheduler(player).runAtFixedRate((task) -> {
                            if (!player.isSneaking()) {
                                task.cancel(); return;
                            }
                            if (heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("CREEPER")).isEmpty()) {
                                task.cancel(); return;
                            }
                            if (!isOnGround(player)) {
                                task.cancel(); return;
                            }
                            if (timer[0] >= creeperData.getFuse()) {
                                explosion(creeperData, player, world);
                                task.cancel();
                            } else {
                                timer[0]++;
                            }
                        }, () -> {}, 1L, 1L);
                    }
                }
            }
        }
    }

    public boolean isOnGround(Player player) {
        return !player.getLocation().clone().subtract(0, 1, 0).getBlock().getType().isAir();
    }

    public void explosion(CreeperData creeperData, Player player, World world) {
        world.createExplosion(player.getLocation(), creeperData.getCreeperPower(), false);
        skillCooldownHandler.startCooldown(creeperData, creeperData.getCooldown(), player.getUniqueId());
        player.setVelocity(new Vector(0, creeperData.getUpwardsVelocity(), 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, creeperData.getSlowfallDuration(), 0));
    }

}
