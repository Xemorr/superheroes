package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;

public class JetpackData extends SkillData {

    @JsonPropertyWithDefault
    boolean gliding = true;
    @JsonPropertyWithDefault
    boolean disableInWater = true;
    @JsonPropertyWithDefault
    boolean sprinting = true;
    @JsonPropertyWithDefault
    boolean disableOnGround = true;
    @JsonPropertyWithDefault
    boolean verticalOnly = true;
    @JsonPropertyWithDefault
    double horizontalMagnitude = 1.0;
    @JsonPropertyWithDefault
    double verticalMagnitude = 1.1;
    @JsonPropertyWithDefault
    double horizontalAccelerationMultiplier = 0.15; // up to 1
    @JsonPropertyWithDefault
    double verticalAccelerationMultiplier = 0.4;

    public double getHorizontalAccelerationMultiplier() {
        return horizontalAccelerationMultiplier;
    }

    public double getVerticalAccelerationMultiplier() {
        return verticalAccelerationMultiplier;
    }

    public boolean glidingMode() {
        return gliding;
    }

    public double getHorizontalMagnitude() {
        return horizontalMagnitude;
    }

    public boolean shouldDisableInWater() {
        return disableInWater;
    }

    public boolean requiresSprinting() {
        return sprinting;
    }

    public boolean shouldDisableOnGround() {
        return disableOnGround;
    }

    public boolean isVerticalOnly() {
        return verticalOnly;
    }

    public double getVerticalMagnitude() {
        return verticalMagnitude;
    }
}
