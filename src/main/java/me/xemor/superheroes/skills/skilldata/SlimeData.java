package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;

public class SlimeData extends SkillData {

    @JsonPropertyWithDefault
    private double speedMultiplier = 1;

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}
