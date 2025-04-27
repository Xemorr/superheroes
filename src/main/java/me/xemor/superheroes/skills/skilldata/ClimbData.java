package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class ClimbData extends SkillData {

    @JsonPropertyWithDefault
    private double proximity = 0.2;
    @JsonPropertyWithDefault
    @JsonAlias("speed")
    private double climbSpeed = 0.2;
    @JsonPropertyWithDefault
    private boolean debug = false;
    @JsonPropertyWithDefault
    private SetData<Material> blocks = new SetData<>();
    @JsonPropertyWithDefault
    private boolean whitelist = true;

    public double getProximity() {
        return proximity * proximity;
    }

    public double getClimbSpeed() {
        return climbSpeed;
    }

    public boolean isDebug() {
        return debug;
    }

    public SetData<Material> getBlocks() {
        return blocks;
    }

    public boolean isWhitelist() {
        return whitelist;
    }
}
