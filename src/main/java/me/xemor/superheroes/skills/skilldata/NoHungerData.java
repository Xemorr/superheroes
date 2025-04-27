package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;

public class NoHungerData extends SkillData {

    @JsonPropertyWithDefault
    private double minimumHunger = 19;

    public double getMinimumHunger() {
        return minimumHunger;
    }
}
