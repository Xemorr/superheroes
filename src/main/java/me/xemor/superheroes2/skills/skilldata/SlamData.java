package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.skilldata.configdata.CooldownData;
import me.xemor.superheroes2.skills.skilldata.configdata.ItemStackData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class SlamData extends CooldownData {

    private ItemStack hand;
    private double airCooldown;
    private int foodCost;
    private int minimumFood;
    private double diameterRadius;
    private double damage;

    public SlamData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection, "&8&lSlam &fCooldown: %currentcooldown% seconds", 10);
        ConfigurationSection handSection = configurationSection.getConfigurationSection("item");
        if (handSection == null) {
            hand = new ItemStack(Material.AIR);
        }
        else {
            hand = new ItemStackData(handSection, "AIR").getItem();
        }
        airCooldown = configurationSection.getDouble("airCooldown", 1);
        foodCost = configurationSection.getInt("foodCost", 0);
        minimumFood = configurationSection.getInt("minimumFood", 0);
        diameterRadius = configurationSection.getDouble("radius", 5) * 2;
        damage = configurationSection.getDouble("damage", 0);
    }

    public ItemStack getHand() {
        return hand;
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

}
