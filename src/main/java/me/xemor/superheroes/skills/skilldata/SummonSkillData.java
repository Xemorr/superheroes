package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import me.xemor.superheroes.skills.skilldata.configdata.Cooldown;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SummonSkillData extends PotionEffectSkillData implements Cooldown {

    @JsonPropertyWithDefault
    private int range = 10;
    @JsonPropertyWithDefault
    private boolean repel = false;
    @JsonPropertyWithDefault
    @JsonAlias({"sneak", "mustsneak"})
    private boolean mustSneak = true;
    @JsonPropertyWithDefault
    private EntityType entityType = EntityType.LIGHTNING_BOLT;
    @JsonPropertyWithDefault
    private Set<Action> action = Set.of(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK);
    @JsonPropertyWithDefault
    private double cooldown = 10D;
    @JsonPropertyWithDefault
    private String cooldownMessage = "<yellow><bold>Zeus <white>Cooldown: <s> seconds";

    public int getRange() {
        return range;
    }

    public boolean mustSneak() {
        return mustSneak;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Set<Action> getAction() {
        return action;
    }

    public boolean doesRepel() {
        return repel;
    }

    public double getCooldown() {
        return cooldown;
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }
}
