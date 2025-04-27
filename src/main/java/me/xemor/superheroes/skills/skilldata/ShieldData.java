package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.Duration;
import me.xemor.configurationdata.JsonPropertyWithDefault;

public class ShieldData extends SkillData {

    @JsonPropertyWithDefault
    private Duration cooldown = new Duration(100D);

    public long getCooldown() {
        return cooldown.getDurationInTicks().orElse(2000L);
    }
}
