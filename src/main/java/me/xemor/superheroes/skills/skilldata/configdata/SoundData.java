package me.xemor.superheroes.skills.skilldata.configdata;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

public class SoundData {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundData(ConfigurationSection configurationSection) {
        sound = Sound.valueOf(configurationSection.getString("sound", "ENTITY_GENERIC_EXPLODE").toUpperCase());
        volume = (float) configurationSection.getDouble("volume", 1.0);
        pitch = (float) configurationSection.getDouble("pitch", 1.0);
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

}
