package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.Duration;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.configuration.ConfigurationSection;

public class CreeperData extends CooldownData {

    @JsonPropertyWithDefault
    private Duration fuse = new Duration(2D);
    @JsonPropertyWithDefault
    @JsonAlias("creeper_power")
    private float creeperPower = 1;
    @JsonPropertyWithDefault
    @JsonAlias("upwards_velocity")
    private double upwardsVelocity = 2.5;
    @JsonPropertyWithDefault
    @JsonAlias("slowfall_duration")
    private Duration slowfallDuration = new Duration(7D);

    public long getFuse() {
        return fuse.getDurationInTicks().orElse(2L * 20L);
    }

    public float getCreeperPower() {
        return creeperPower;
    }

    public double getUpwardsVelocity() {
        return upwardsVelocity;
    }

    public long getSlowfallDuration() {
        return slowfallDuration.getDurationInTicks().orElse(7 * 20L);
    }
}
