package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;

public class ElectrifiedData extends PotionEffectData {

    private double damageResistance;

    protected ElectrifiedData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        Double dR = configurationSection.getDouble("damageResistance");
        if (dR != null) {
            damageResistance = dR.doubleValue();
        }
        else {
            damageResistance = 0;
        }
    }

    public double getDamageResistance() {
        return damageResistance;
    }

}
