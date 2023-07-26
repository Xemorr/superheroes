package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.stream.Collectors;

public class RepulsionData extends SkillData {

    private final double multiplier;
    private final double radius;
    private final Set<EntityType> blacklist;

    public RepulsionData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        this.multiplier = configurationSection.getDouble("multiplier", 1);
        this.radius = configurationSection.getDouble("radius", 5.0);
        this.blacklist = configurationSection.getStringList("entityBlacklist").stream().map(EntityType::valueOf).collect(Collectors.toSet());
    }

    public double getMultiplier() {
        return multiplier;
    }

    public double getRadius() {
        return radius;
    }

    public boolean inBlacklist(EntityType entityType) {
        return blacklist.contains(entityType);
    }
}
