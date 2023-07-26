package me.xemor.superheroes.skills.skilldata;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class EggLayerData extends SkillData {

    private final ItemStack toLay;
    private final long tickDelay;

    public EggLayerData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        String materialStr = configurationSection.getString("type", "EGG");
        int quantity = configurationSection.getInt("quantity", 1);
        toLay = new ItemStack(Material.valueOf(materialStr), quantity);
        tickDelay = Math.round(configurationSection.getDouble("delay", 15) * 20);
    }

    public ItemStack getToLay() {
        return toLay;
    }

    public long getTickDelay() {
        return tickDelay;
    }
}
