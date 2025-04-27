package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.SoundData;
import me.xemor.configurationdata.comparison.ItemComparisonData;
import me.xemor.configurationdata.particles.ParticleData;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;

public class GunData extends CooldownData {

    @JsonPropertyWithDefault
    private double damage = 5.0;
    @JsonPropertyWithDefault
    private double maxDistance = 64;
    @JsonPropertyWithDefault
    private double bulletSize = 1.0;
    @JsonPropertyWithDefault
    private ItemComparisonData item = new ItemComparisonData();
    @JsonPropertyWithDefault
    private SoundData shootSound;
    @JsonPropertyWithDefault
    private ParticleData trailParticle;
    @JsonPropertyWithDefault
    private ParticleData hitParticle;

    public double getDamage() {
        return damage;
    }

    public ItemComparisonData getItemComparison() {
        return item;
    }

    public SoundData getShootSound() {
        return shootSound;
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
