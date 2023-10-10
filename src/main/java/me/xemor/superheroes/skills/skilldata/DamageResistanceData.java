package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.PotionEffectData;
import me.xemor.superheroes.skills.skilldata.exceptions.InvalidConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DamageResistanceData extends SkillData {

    private final double damageMultiplier;
    @Nullable
    private PotionEffectData potionEffect;
    private HashSet<EntityDamageEvent.DamageCause> damageCauses;

    public DamageResistanceData(int skill, ConfigurationSection configurationSection) throws InvalidConfig {
        super(skill, configurationSection);
        damageMultiplier = configurationSection.getDouble("damageMultiplier", 0);
        List<String> damageCausesStr = configurationSection.getStringList("damageCause");
        if (damageCausesStr.contains("ALL")) {
            damageCauses = null;
        }
        damageCauses = damageCausesStr.stream().map(EntityDamageEvent.DamageCause::valueOf).collect(Collectors.toCollection(HashSet::new));
        if (configurationSection.contains("type")) {
            PotionEffectType type = PotionEffectType.getByName(configurationSection.getString("type", ""));
            if (type == null) {
                throw new InvalidConfig("Invalid potion effect type specified in damage resistance skill " + configurationSection.getCurrentPath());
            }
            potionEffect = new PotionEffectData(configurationSection);
        }
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public Optional<PotionEffect> getPotionEffect() {
        if (potionEffect == null) return Optional.empty();
        return potionEffect.getPotionEffect();
    }

    public HashSet<EntityDamageEvent.DamageCause> getDamageCause() {
        return damageCauses;
    }
}
