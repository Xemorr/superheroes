package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;

public class ElectrifiedData extends PotionEffectData {

    private double damageResistance;

    protected ElectrifiedData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        damageResistance = configurationSection.getDouble("damageResistance", 0);
    }

    public double getDamageResistance() {
        return damageResistance;
    }

}
