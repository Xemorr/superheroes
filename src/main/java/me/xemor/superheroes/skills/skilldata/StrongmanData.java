package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class StrongmanData extends SkillData {

    private final double velocity;
    private final double upwardsVelocity;
    private final String tooMuscularMessage;
    private final int maxPassengers;

    public StrongmanData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        velocity = configurationSection.getDouble("velocity", 2.5);
        upwardsVelocity = configurationSection.getDouble("upwardsVelocity", 1);
        tooMuscularMessage = configurationSection.getString("tooMuscularMessage", "<player> <white> is too strong to sit in a vehicle!");
        maxPassengers = configurationSection.getInt("maxPassengers", 10);
    }

    public double getVelocity() {
        return velocity;
    }

    public double getUpwardsVelocity() {
        return upwardsVelocity;
    }

    public String getTooMuscularMessage() {
        return tooMuscularMessage;
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }
}
