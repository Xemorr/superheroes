package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.ItemStackData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class SlamData extends CooldownData {

    @JsonPropertyWithDefault
    @JsonAlias("item")
    private ItemStack hand = new ItemStack(Material.AIR);
    @JsonPropertyWithDefault
    private double airCooldown = 1;
    @JsonPropertyWithDefault
    private int foodCost = 0;
    @JsonPropertyWithDefault
    private int minimumFood = 0;
    @JsonPropertyWithDefault
    @JsonAlias("diameterRadius")
    private double radius = 5;
    @JsonPropertyWithDefault
    private double damage = 0;

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

    public double getDiameter() {
        return radius * 2;
    }

    public double getDamage() {
        return damage;
    }

}
