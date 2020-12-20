package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class DamageResistanceData extends PotionEffectData {

    private double damageMultiplier;
    private HashSet<EntityDamageEvent.DamageCause> damageCauses;

    protected DamageResistanceData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        damageMultiplier = configurationSection.getDouble("damageMultiplier", 0);
        List<String> damageCausesStr = configurationSection.getStringList("damageCause");
        if (damageCausesStr.contains("ALL")) {
            damageCauses = null;
        }
        damageCauses = damageCausesStr.stream().map(str -> EntityDamageEvent.DamageCause.valueOf(str)).collect(Collectors.toCollection(HashSet::new));
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public HashSet<EntityDamageEvent.DamageCause> getDamageCause() {
        return damageCauses;
    }
}
