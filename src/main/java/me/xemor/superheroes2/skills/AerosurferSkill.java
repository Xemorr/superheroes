package me.xemor.superheroes2.skills;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.skilldata.AerosurferData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class AerosurferSkill extends SkillImplementation {
    FixedMetadataValue fixedMetadataValue;
    public AerosurferSkill(PowersHandler powersHandler) {
        super(powersHandler);
        fixedMetadataValue = new FixedMetadataValue(powersHandler.getPlugin(), true);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = getPowersHandler().getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.AEROSURFER);
        for (SkillData skillData : skillDatas) {
            AerosurferData data = (AerosurferData) skillData;
            if (e.isSneaking()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player == null) {
                            cancel();
                            return;
                        }
                        if (!player.isOnline()) {
                            cancel();
                            return;
                        }
                        if (powersHandler.getSuperhero(player).hasSkill(Skill.AEROSURFER)) {
                            cancel();
                            return;
                        }
                        if (!player.isSneaking()) {
                            cancel();
                            return;
                        }
                        Location blockToPlaceLocation = e.getPlayer().getLocation().add(0, -1, 0);
                        World world = e.getPlayer().getWorld();
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 4, 1));
                        Block block = world.getBlockAt(blockToPlaceLocation);
                        if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                            block.setType(data.getBlock());
                            block.setMetadata("aerosurferGlass", fixedMetadataValue);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (block.getType() == data.getBlock()) {
                                        world.playSound(block.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5F, 1.0F);
                                        block.setType(Material.AIR);
                                    }
                                }
                            }.runTaskLater(powersHandler.getPlugin(), 200L);
                        }
                    }
                }.runTaskTimer(powersHandler.getPlugin(), 0L, 2L);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().hasMetadata("aerosurferGlass")) {
            e.setDropItems(false);
        }
    }
}
