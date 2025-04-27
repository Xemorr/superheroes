package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.sepdron.headcreator.HeadCreator;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class DecoyData extends SkillData {

    @JsonPropertyWithDefault
    private Color color;
    @JsonPropertyWithDefault
    @JsonAlias("base64skin")
    private String base64Skin = "SELF";

    public Color getColor() {
        return color;
    }

    public ItemStack getSkull(Player player) {
        ItemStack skull;
        if ("SELF".equals(base64Skin)) skull = null;
        else {
            skull = HeadCreator.createFromBase64(base64Skin);
        }
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
