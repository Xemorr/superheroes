package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;

public class PickpocketData extends SkillData {

    @JsonPropertyWithDefault
    private double range = 3;
    @JsonPropertyWithDefault
    @JsonAlias("sneaking")
    private boolean isSneaking = true;

    public double getRangeSquared() {
        return range * range;
    }

    public boolean isSneaking() {
        return isSneaking;
    }
}
