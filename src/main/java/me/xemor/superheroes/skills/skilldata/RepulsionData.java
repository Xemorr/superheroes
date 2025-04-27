package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RepulsionData extends SkillData {

    @JsonPropertyWithDefault
    private double multiplier = 1;
    @JsonPropertyWithDefault
    private double radius = 5.0;
    @JsonPropertyWithDefault
    @JsonAlias("entityBlacklist")
    private Set<EntityType> blacklist = new HashSet<>();

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
