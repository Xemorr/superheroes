package me.xemor.superheroes2.skills.skilldata.configdata;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PotionEffectData {

    private PotionEffect potionEffect;

    public PotionEffectData(ConfigurationSection configurationSection, @NotNull PotionEffectType defaultType, int defaultDuration, int defaultPotency) {
        int potency = configurationSection.getInt("potency", defaultPotency);
        if (potency > 0) {
            potency--;
        }
        PotionEffectType potionType = PotionEffectType.getByName(configurationSection.getString("type", "d").toUpperCase());
        if (potionType == null) {
            if (defaultType == null) return;
            potionType = defaultType;
        }
        if (potionType != null) {
            double duration = configurationSection.getDouble("duration", defaultDuration);
            createPotion(potionType, duration, potency);
        }
    }

    public PotionEffect getPotionEffect() {
        return potionEffect;
    }

    protected void createPotion(@NotNull PotionEffectType type, double duration, int potency) {
        if (type.isInstant()) {
            potionEffect = new PotionEffect(type, 1, potency);
        }
        else if (duration != 0) {
            potionEffect = new PotionEffect(type, (int) Math.round(duration * 20), potency);
        }
        else {
            potionEffect = new PotionEffect(type, Integer.MAX_VALUE, potency);
        }
    }

}
