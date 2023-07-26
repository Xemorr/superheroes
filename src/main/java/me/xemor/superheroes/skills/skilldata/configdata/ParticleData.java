package me.xemor.superheroes.skills.skilldata.configdata;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

public class ParticleData {

    private final boolean isHelix;
    private final double radius;
    private final int duration;
    private final int numberOfParticles;
    private final Particle particle;

    public ParticleData(ConfigurationSection configurationSection) {
        isHelix = configurationSection.getBoolean("isHelix", true);
        radius = configurationSection.getDouble("radius", 1);
        duration = (int) Math.round(configurationSection.getDouble("duration", 2.5D) * 20);
        particle = Particle.valueOf(configurationSection.getString("particleType", "PORTAL"));
        numberOfParticles = configurationSection.getInt("numberOfParticles", 1);
    }

    public boolean isHelix() {
        return isHelix;
    }

    public double getRadius() {
        return radius;
    }

    public int getDuration() {
        return duration;
    }

    public Particle getParticle() {
        return particle;
    }

    public int getNumberOfParticles() { return numberOfParticles; }
}
