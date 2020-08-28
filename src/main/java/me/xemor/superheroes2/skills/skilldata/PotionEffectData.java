package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectData extends SkillData {

    PotionEffect potionEffect;

    protected PotionEffectData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        int potency = configurationSection.getInt("potency", 0);
        if (potency != 0) {
            potency--;
        }
        String potionType = configurationSection.getString("type", null);
        if (potionType == null) {
            return;
        }
        PotionEffectType type = PotionEffectType.getByName(potionType.toUpperCase());
        int duration = configurationSection.getInt("duration", 0);
        createPotion(type, duration, potency);
    }

    public PotionEffect getPotionEffect() {
        return potionEffect;
    }

    protected void createPotion(PotionEffectType type, int duration, int potency) {
        if (type.isInstant()) {
            potionEffect = new PotionEffect(type, 1, potency);
        }
        else if (duration != 0) {
            potionEffect = new PotionEffect(type, duration * 20, potency);
        }
        else {
            potionEffect = new PotionEffect(type, Integer.MAX_VALUE, potency);
        }
    }
}
