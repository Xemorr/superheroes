package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;

public class SneakData extends SkillData {

    @JsonPropertyWithDefault
    @JsonAlias("sneak")
    private boolean mustSneak;
    @JsonPropertyWithDefault
    private boolean needsInvisibility;

    public boolean mustSneak() {
        return mustSneak;
    }

    public boolean needsInvisibility() {
        return needsInvisibility;
    }
}
