package me.xemor.superheroes.skills.skilldata;

import me.sepdron.headcreator.HeadCreator;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class DecoyData extends SkillData {

    private final Color color;
    private final ItemStack skull;

    public DecoyData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        ConfigurationSection colorSection = configurationSection.getConfigurationSection("color");
        int red = colorSection.getInt("red", 0);
        int green = colorSection.getInt("green", 0);
        int blue = colorSection.getInt("blue", 0);
        color = Color.fromRGB(red, green, blue);
        String base64skin = configurationSection.getString("base64Skin", "SELF");
        if ("SELF".equals(base64skin)) skull = null;
        else {
            skull = HeadCreator.createFromBase64(base64skin);
        }
    }

    public Color getColor() {
        return color;
    }

    public ItemStack getSkull(Player player) {
        if (skull == null) {
            ItemStack newSkull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) newSkull.getItemMeta();
            skullMeta.setOwningPlayer(player);
            newSkull.setItemMeta(skullMeta);
            return newSkull;
        }
        return skull;
    }


}
