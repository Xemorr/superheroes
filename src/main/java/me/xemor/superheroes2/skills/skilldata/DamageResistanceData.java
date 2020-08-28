package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageResistanceData extends PotionEffectData {

    private double damageMultiplier;
    private EntityDamageEvent.DamageCause damageCause;

    protected DamageResistanceData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        damageMultiplier = configurationSection.getDouble("damageMultiplier", 0);
        String damageCauseStr = configurationSection.getString("damageCause", "ALL");
        if ("ALL".equals(damageCauseStr)) {
            damageCause = null;
        }
        else {
            damageCause = EntityDamageEvent.DamageCause.valueOf(damageCauseStr);
        }
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
    }

}
