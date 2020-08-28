package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class SlamData extends SkillData {

    private Material hand;
    private double landCooldown;
    private double airCooldown;
    private int foodCost;
    private int minimumFood;
    private double diameterRadius;
    private double damage;
    private String cooldownMessage;

    protected SlamData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        hand = Material.valueOf(configurationSection.getString("item", "AIR").toUpperCase());
        landCooldown = configurationSection.getDouble("landCooldown", 10);
        airCooldown = configurationSection.getDouble("airCooldown", 1);
        foodCost = configurationSection.getInt("foodCost", 0);
        minimumFood = configurationSection.getInt("minimumFood", 0);
        diameterRadius = configurationSection.getDouble("radius", 5) * 2;
        damage = configurationSection.getDouble("damage", 0);
        cooldownMessage = ChatColor.translateAlternateColorCodes('&', configurationSection.getString("cooldownMessage", "&8&lSlam &fCooldown: %s seconds"));
    }

    public Material getHand() {
        return hand;
    }

    public double getLandCooldown() {
        return landCooldown;
    }

    public double getAirCooldown() {
        return airCooldown;
    }

    public int getFoodCost() {
        return foodCost;
    }

    public int getMinimumFood() {
        return minimumFood;
    }

    public double getDiameterRadius() {
        return diameterRadius;
    }

    public double getDamage() {
        return damage;
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }

}
