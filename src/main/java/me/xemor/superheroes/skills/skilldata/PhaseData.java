package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;

public class PhaseData extends SkillData {

    @JsonPropertyWithDefault
    @JsonAlias("minimumPhaseYAxis")
    private double numberOfBlocksPhasingAboveWorldMinHeight = 5;

    public double getNumberOfBlocksPhasingAboveWorldMinHeight() {
        return numberOfBlocksPhasingAboveWorldMinHeight;
    }
}
