package me.xemor.superheroes.skills.skilldata;

import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.stream.Collectors;

public class RemoteDetonationData extends CooldownData {

    final private boolean spawnsFire;
    final private boolean breakBlocks;
    final private boolean removeDetonatedEntity;
    final private float explosionStrength;
    final private List<EntityType> explodable;

    public RemoteDetonationData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection, "You have <s> seconds remaining before you can remote detonate again", 10);
        explosionStrength = (float) configurationSection.getDouble("explosionStrength", 1);
        explodable = configurationSection.getStringList("explodable").stream().map(EntityType::valueOf).collect(Collectors.toList());
        spawnsFire = configurationSection.getBoolean("spawnsFire", false);
        breakBlocks = configurationSection.getBoolean("breakBlocks", true);
        removeDetonatedEntity = configurationSection.getBoolean("removeDetonatedEntity", true);
    }

    public float getExplosionStrength() {
        return explosionStrength;
    }

    public List<EntityType> getExplodable() {
        return explodable;
    }

    public boolean spawnsFire() {
        return spawnsFire;
    }

    public boolean breakBlocks() {
        return breakBlocks;
    }

    public boolean removeDetonatedEntity() {
        return removeDetonatedEntity;
    }
}
