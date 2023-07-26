package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.ItemStackData;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class SlamData extends CooldownData {

    private final ItemStack hand;
    private final double airCooldown;
    private final int foodCost;
    private final int minimumFood;
    private final double diameterRadius;
    private final double damage;

    public SlamData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection, "<dark_gray><bold>Slam <white>Cooldown: <currentcooldown> seconds", 10);
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
