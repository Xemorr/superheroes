package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class PhaseSkill extends SkillImplementation {
    public PhaseSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            if (!onGround(e.getPlayer())) {
                return;
            }
            Player player = e.getPlayer();
            Superhero superhero = heroHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData(Skill.PHASE);
            for (SkillData ignored : skillDatas) {
                player.setVelocity(new Vector(0, -0.1, 0));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!heroHandler.getSuperhero(player).equals(superhero)) {
                            cancel();
                            player.setGameMode(GameMode.SURVIVAL);
                            return;
                        }
                        if (player.isSneaking() && player.getLocation().getY() > 5) {
                            player.setGameMode(GameMode.SPECTATOR);
                            player.setGravity(true);
                            player.setAllowFlight(false);
                            player.setFlying(false);
                            try {
                                player.setVelocity(player.getVelocity().normalize());
                            }
                            catch(IllegalArgumentException e) {
                            }
                        }
                        else {
                            if (!player.getAllowFlight()) {
                                player.setGameMode(GameMode.SURVIVAL);
                                player.removePotionEffect(PotionEffectType.BLINDNESS);
                                player.setVelocity(new Vector(0, 1.33, 0));
                                player.teleport(player.getEyeLocation().add(0, 0.35, 0));
                                if (player.getWorld().getBlockAt(player.getLocation()).isPassable()) {
                                    cancel();
                                }
                            }
                        }
                    }
                }.runTaskTimer(heroHandler.getPlugin(), 0L, 3L);
            }
        }
    }

    public boolean onGround(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();
        Block blockBelow = world.getBlockAt(location).getRelative(BlockFace.DOWN);
        if (blockBelow.isEmpty()) {
            return false;
        }
        return true;
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE && heroHandler.getSuperhero(e.getPlayer()).hasSkill(Skill.PHASE)) {
            e.setCancelled(true);
        }
    }

/*
    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = e.getPlayer();
            Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.PHASE);
            for (SkillData skillData : skillDatas) {
                PhaseData phaseData = (PhaseData) skillData;
                Block block = player.getLocation().getBlock();
                CraftBlock craftBlock = (CraftBlock) block;
                Block block2 = block.getRelative(BlockFace.DOWN);
                CraftBlock craftBlock2 = (CraftBlock) block2;
                CraftPlayer craftPlayer = (CraftPlayer) player;
                EntityPlayer handle = craftPlayer.getHandle();
                handle.noclip = true;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        handle.noclip = true;
                    }
                }.runTaskTimer(powersHandler.getPlugin(), 0L, 1L);
                PacketPlayOutBlockChange packetPlayOutBlockBreak = new PacketPlayOutBlockChange(new BlockPosition(block.getX(), block.getY(), block.getZ()), CraftMagicNumbers.getBlock(Material.AIR, (byte) 0));
                PacketPlayOutBlockChange packetPlayOutBlockBreak2 = new PacketPlayOutBlockChange(new BlockPosition(block.getX(), block.getY() - 1, block.getZ()), CraftMagicNumbers.getBlock(Material.AIR, (byte) 0));
                handle.playerConnection.sendPacket(packetPlayOutBlockBreak);
                handle.playerConnection.sendPacket(packetPlayOutBlockBreak2);
            }
        }
    }
*/

}
