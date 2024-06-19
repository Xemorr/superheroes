package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.ClimbData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ClimbSkill extends SkillImplementation {

    private final static List<BlockFace> faces = List.of(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH);
    private final static double root2Over2 = Math.sqrt(2) / 2;

    public ClimbSkill(HeroHandler heroHandler) {
        super(heroHandler);
        Superheroes.getScheduling().globalRegionalScheduler().runAtFixedRate(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Superheroes.getScheduling().entitySpecificScheduler(player).run(() -> performClimbSkill(player), () -> {});
            }
        }, 10L, 1L);
    }

    public void performClimbSkill(Player player) {
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("CLIMB"));
        for (SkillData skillData : skillDatas) {
            if (skillData.areConditionsTrue(player)) {
                ClimbData climbData = (ClimbData) skillData;
                if (climbData.isDebug()) {
                    for (int i = 0; i < 1000; i++) {
                        Random random = new Random();
                        double rngX = random.nextDouble(4) - 2;
                        double rngZ = random.nextDouble(4) - 2;
                        Location currentLoc = player.getLocation();
                        Location newLoc = currentLoc.clone().add(rngX, 0, rngZ);
                        if (rotatedManhattanDistance(newLoc, currentLoc) < climbData.getProximity()) {
                            World world = newLoc.getWorld();
                            world.spawnParticle(Particle.DRIPPING_LAVA, newLoc, 1);
                        }
                    }
                }
                if (isClimbing(player, climbData)) {
                    Vector finalVelocity = player.getVelocity().setY(climbData.getClimbSpeed());
                    player.setVelocity(finalVelocity);
                }
            }
        }
    }

    public boolean isClimbing(Player player, ClimbData climbData) {
        for (BlockFace face : faces) {
            Location location = player.getLocation();
            Block adjacent = location.getBlock().getRelative(face);
            BlockData data = adjacent.getBlockData();
            if (data instanceof Slab slab && slab.getType() == Slab.Type.BOTTOM) {
                return false;
            }
            if (data instanceof Stairs stairs && (stairs.getFacing() != face.getOppositeFace())) {
                return false;
            }
            if ((climbData.isWhitelist() && climbData.getBlocks().inSet(adjacent.getType())) || (!climbData.isWhitelist() && !climbData.getBlocks().inSet(adjacent.getType()))) {
                if (adjacent.getType().isSolid()) {
                    Location adjacentCentre = adjacent.getLocation().add(0.5, 0, 0.5);
                    if (climbData.getProximity() > rotatedManhattanDistance(location, adjacentCentre)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onVelocityChange(PlayerVelocityEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("CLIMB"));
        for (SkillData skillData : skillDatas) {
            if (skillData.areConditionsTrue(player)) {
                ClimbData climbData = (ClimbData) skillData;
                if (isClimbing(player, climbData)) {
                    if (e.getVelocity().getY() > 0 && e.getVelocity().getY() < climbData.getClimbSpeed()) {
                        e.setVelocity(e.getVelocity().clone().setY(climbData.getClimbSpeed()));
                    }
                }
            }
        }
    }

    public double rotatedManhattanDistance(Location a, Location b) {
        a = rotateLocation45Degrees(a);
        b = rotateLocation45Degrees(b);
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getZ() - b.getZ());
    }

    public Location rotateLocation45Degrees(Location loc) {
        return new Location(loc.getWorld(), root2Over2 * loc.getX() - root2Over2 * loc.getZ(), loc.getY(), root2Over2 * loc.getX() + root2Over2 * loc.getZ());
    }
}
