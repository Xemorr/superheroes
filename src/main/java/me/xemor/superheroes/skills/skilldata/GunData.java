package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.ParticleData;
import me.xemor.configurationdata.SoundData;
import me.xemor.configurationdata.comparison.ItemComparisonData;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.configuration.ConfigurationSection;

public class GunData extends CooldownData {

    private final double damage;
    private final double maxDistance;
    private final double bulletSize;
    private ItemComparisonData itemStackData;
    private SoundData shootSoundData;
    private ParticleData trailParticle;
    private ParticleData hitParticle;

    public GunData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection, "<dark_grey><bold>Gun <grey>has <s> seconds left until it can be used again!", 2);
        maxDistance = configurationSection.getDouble("maxDistance", 64);
        bulletSize = configurationSection.getDouble("bulletSize", 1.0);
        damage = configurationSection.getDouble("damage", 5.0);
        ConfigurationSection itemSection = configurationSection.getConfigurationSection("item");
        if (itemSection != null) {
            itemStackData = new ItemComparisonData(itemSection);
        }
        ConfigurationSection shootSoundSection = configurationSection.getConfigurationSection("shootSound");
        if (shootSoundSection != null) {
            shootSoundData = new SoundData(shootSoundSection);
        }
        ConfigurationSection trailParticleSection = configurationSection.getConfigurationSection("trailParticle");
        if (trailParticleSection != null) {
            trailParticle = new ParticleData(trailParticleSection);
        }
        ConfigurationSection hitParticleSection = configurationSection.getConfigurationSection("hitParticle");
        if (hitParticleSection != null) {
            hitParticle = new ParticleData(hitParticleSection);
        }
    }

    public double getDamage() {
        return damage;
    }

    public ItemComparisonData getItemStackData() {
        return itemStackData;
    }

    public SoundData getShootSoundData() {
        return shootSoundData;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public double getBulletSize() {
        return bulletSize;
    }

    public ParticleData getTrailParticle() {
        return trailParticle;
    }

    public ParticleData getHitParticle() {
        return hitParticle;
    }
}
