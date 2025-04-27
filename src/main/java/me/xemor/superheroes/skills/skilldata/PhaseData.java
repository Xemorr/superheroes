package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;

public class PhaseData extends SkillData {

    @JsonPropertyWithDefault
    private double minimumPhaseYAxis = 5;

    public double getMinimumPhaseYAxis() {
        return minimumPhaseYAxis;
    }
}
