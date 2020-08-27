package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;

public class PhaseData extends SkillData {

    private double minimumPhaseYAxis;

    protected PhaseData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        minimumPhaseYAxis = configurationSection.getDouble("minimumPhaseYAxis", 5);
    }

    public double getMinimumPhaseYAxis() {
        return minimumPhaseYAxis;
    }
}
