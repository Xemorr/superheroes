package me.xemor.superheroes.skills.implementations;

import io.papermc.lib.PaperLib;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.PhaseData;
import me.xemor.superheroes.skills.skilldata.SkillData;
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
import space.arim.morepaperlib.scheduling.ScheduledTask;

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
            Collection<PhaseData> phaseDatas = superhero.getSkillData(PhaseData.class);
            for (PhaseData phaseData : phaseDatas) {
                player.setVelocity(new Vector(0, -0.1, 0));
                Superheroes.getScheduling().entitySpecificScheduler(player).runAtFixedRate(
                        (task) -> {
                            if (!heroHandler.getSuperhero(player).equals(superhero)) {
                                task.cancel();
                                player.setGameMode(GameMode.SURVIVAL);
                                return;
                            }
                            if (player.isSneaking() && player.getLocation().getY() > player.getWorld().getMinHeight() + 5) {
                                phaseData.areConditionsTrue(player).thenAccept((b) -> {
                                    if (b) {
                                        player.setGameMode(GameMode.SPECTATOR);
                                        player.setGravity(true);
                                        player.setAllowFlight(false);
                                        player.setFlying(false);
                                        try {
                                            player.setVelocity(player.getVelocity().normalize());
                                        }
                                        catch(IllegalArgumentException ignored) {} // vector of 0 length
                                    }
                                    else elsePath(player, task);
                                });
                            }
                            else elsePath(player, task);
                        },
                        () -> {},
                        1L, 3L
                );
            }
        }
    }

    public void elsePath(Player player, ScheduledTask task) {
        if (!player.getAllowFlight()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.setVelocity(new Vector(0, 1.33, 0));
            PaperLib.teleportAsync(player, player.getEyeLocation().add(0, 0.35, 0));
            if (player.getWorld().getBlockAt(player.getLocation()).isPassable()) {
                task.cancel();
            }
        }
    }

    public boolean onGround(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();
        Block blockBelow = world.getBlockAt(location).getRelative(BlockFace.DOWN);
        return !blockBelow.isEmpty();
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE && heroHandler.getSuperhero(e.getPlayer()).hasSkill("PHASE")) {
            e.setCancelled(true);
        }
    }

}
