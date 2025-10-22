package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.entity.EntityType;

import java.util.HashSet;
import java.util.Set;

public class RepulsionData extends SkillData {

    @JsonPropertyWithDefault
    private double multiplier = 1;
    @JsonPropertyWithDefault
    private double radius = 5.0;
    @JsonPropertyWithDefault
    @JsonAlias({"entityBlacklist", "blacklist"})
    private Set<EntityType> entities = new HashSet<>();
    @JsonPropertyWithDefault
    private boolean whitelist = false;

    public double getMultiplier() {
        return multiplier;
    }

    public double getRadius() {
        return radius;
    }

    public boolean allow(EntityType entityType) {
        return (!whitelist && !entities.contains(entityType)) || (whitelist && (entities.contains(entityType) || entities.isEmpty()));
    }
}
