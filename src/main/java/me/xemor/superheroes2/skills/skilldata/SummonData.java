package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;

import java.util.HashSet;
import java.util.stream.Collectors;

public class SummonData extends PotionEffectData {

    private int range;
    private boolean repel;
    private boolean mustSneak;
    private EntityType entityType;
    private HashSet<Action> action;
    private double cooldown;
    private String cooldownMessage;

    protected SummonData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        entityType = EntityType.valueOf(configurationSection.getString("entity", "LIGHTNING"));
        range = configurationSection.getInt("range");
        action = configurationSection.getStringList("action").stream().map(str -> Action.valueOf(str)).collect(Collectors.toCollection(HashSet::new));
        if (action.isEmpty()) {
            action = new HashSet<>();
            action.add(Action.LEFT_CLICK_AIR);
            action.add(Action.LEFT_CLICK_BLOCK);
        }
        mustSneak = configurationSection.getBoolean("mustSneak", true);
        repel = configurationSection.getBoolean("repel", false);
        cooldown = configurationSection.getDouble("cooldown", 10D);
        cooldownMessage = ChatColor.translateAlternateColorCodes('&', configurationSection.getString("cooldownMessage", "&e&lZeus &fCooldown: %s seconds"));
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

    public HashSet<Action> getAction() {
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
