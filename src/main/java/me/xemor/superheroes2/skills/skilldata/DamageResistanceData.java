package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.skilldata.configdata.PotionEffectData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class DamageResistanceData extends SkillData {

    private double damageMultiplier;
    private PotionEffect potionEffect;
    private HashSet<EntityDamageEvent.DamageCause> damageCauses;

    public DamageResistanceData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        damageMultiplier = configurationSection.getDouble("damageMultiplier", 0);
        List<String> damageCausesStr = configurationSection.getStringList("damageCause");
        if (damageCausesStr.contains("ALL")) {
            damageCauses = null;
        }
        damageCauses = damageCausesStr.stream().map(EntityDamageEvent.DamageCause::valueOf).collect(Collectors.toCollection(HashSet::new));
        potionEffect = new PotionEffectData(configurationSection, null, 0, 0).getPotionEffect();
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public PotionEffect getPotionEffect() {
        return potionEffect;
    }

    public HashSet<EntityDamageEvent.DamageCause> getDamageCause() {
        return damageCauses;
    }
}
