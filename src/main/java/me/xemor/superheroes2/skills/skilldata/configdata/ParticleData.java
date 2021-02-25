package me.xemor.superheroes2.skills.skilldata.configdata;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

public class ParticleData {

    private Particle particle;
    private int numberOfParticles;

    public ParticleData(ConfigurationSection configurationSection) {
        particle = Particle.valueOf(configurationSection.getString("particle", "BARRIER").toUpperCase());
        numberOfParticles = configurationSection.getInt("numberOfParticles", 1);
    }

    public Particle getParticle() {
        return particle;
    }

    public int getNumberOfParticles() {
        return numberOfParticles;
    }

}
