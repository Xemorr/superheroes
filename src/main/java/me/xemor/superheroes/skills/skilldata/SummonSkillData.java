package me.xemor.superheroes.skills.skilldata;

import me.xemor.superheroes.skills.skilldata.configdata.Cooldown;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SummonSkillData extends PotionEffectSkillData implements Cooldown {

    private final int range;
    private final boolean repel;
    private final boolean mustSneak;
    private final EntityType entityType;
    private Set<Action> action;
    private final double cooldown;
    private final String cooldownMessage;

    public SummonSkillData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        entityType = EntityType.valueOf(configurationSection.getString("entity", "LIGHTNING"));
        range = configurationSection.getInt("range", 10);
        action = configurationSection.getStringList("action").stream().map(Action::valueOf).collect(Collectors.toCollection(HashSet::new));
        if (action.isEmpty()) {
            action = new HashSet<>();
            action.add(Action.LEFT_CLICK_AIR);
            action.add(Action.LEFT_CLICK_BLOCK);
        }
        mustSneak = configurationSection.getBoolean("mustSneak", true);
        repel = configurationSection.getBoolean("repel", false);
        cooldown = configurationSection.getDouble("cooldown", 10D);
        cooldownMessage = configurationSection.getString("cooldownMessage", "<yellow><bold>Zeus <white>Cooldown: <s> seconds");
    }

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
