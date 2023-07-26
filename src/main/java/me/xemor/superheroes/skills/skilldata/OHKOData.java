package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.stream.Collectors;

public class OHKOData extends SkillData {

    private final Set<EntityType> entityTypes;
    private final String displayName;

    public OHKOData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        entityTypes = configurationSection.getStringList("entityTypes").stream().map(string -> EntityType.valueOf(string.toUpperCase())).collect(Collectors.toSet());
        displayName = configurationSection.getString("displayName");
    }

    public Set<EntityType> getEntityTypes() {
        return entityTypes;
    }

    public String getDisplayName() {
        return displayName;
    }




}
