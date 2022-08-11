package me.xemor.superheroes;

import me.xemor.superheroes.skills.skilldata.configdata.ParticleData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleHandler extends BukkitRunnable {

    Player player;
    boolean helix = false;
    Particle particle = null;
    double radius = 1;
    int duration = 0;
    int currentDuration = 0;

    public ParticleHandler(Player player) {
        this.player = player;
    }

    public void setupFromParticleData(ParticleData particleData) {
        setRadius(particleData.getRadius());
        setDuration(particleData.getDuration());
        setParticle(particleData.getParticle());
        setHelix(particleData.isHelix());
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setHelix(boolean helix) {
        this.helix = helix;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void doHelix(int currentDuration) {
        Location location = player.getLocation();
        for (double y = 0; y <= 5; y+=0.15) {
            double trigInput = 4 * (y + currentDuration);
            double x = radius * Math.cos(trigInput);
            double z = radius * Math.sin(trigInput);
            World world = player.getWorld();
            world.spawnParticle(particle, new Location(world, location.getX() + x, location.getY() + y, location.getZ() + z), 1);
        }
    }

    public void doOtherHelix(int currentDuration) {
        Location location = player.getLocation();
        for (double y = 0; y <= 5; y+=0.15) {
            Double trigInput = 4 * (y + currentDuration);
            double x = radius * Math.sin(trigInput);
            double z = radius * Math.cos(trigInput);
            World world = player.getWorld();
            world.spawnParticle(particle, new Location(world, location.getX() + x, location.getY() + y, location.getZ() + z), 1);
        }
    }

    @Override
    public void run() {
        currentDuration += 5;
        if (helix) {
            doHelix(currentDuration);
        }
        if (currentDuration >= duration) {
            cancel();
        }
    }
}
