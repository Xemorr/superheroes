package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class PhaseData extends SkillData {

    private final double minimumPhaseYAxis;

    public PhaseData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        minimumPhaseYAxis = configurationSection.getDouble("minimumPhaseYAxis", 5);
    }

    public double getMinimumPhaseYAxis() {
        return minimumPhaseYAxis;
    }
}
