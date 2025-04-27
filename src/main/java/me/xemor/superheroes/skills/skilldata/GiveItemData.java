package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.ItemStackData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class GiveItemData extends SkillData {

    @JsonPropertyWithDefault
    private ItemStack item;
    @JsonPropertyWithDefault
    private boolean canStore = true;
    @JsonPropertyWithDefault
    private boolean canDrop = true;
    @JsonPropertyWithDefault
    private boolean canLoseOnDeath = true;
    @JsonPropertyWithDefault
    private boolean loseItemOnHeroLoss = true;
    @JsonPropertyWithDefault
    @JsonAlias("canDropOnDeath")
    private boolean dropsOnDeath = true;

    public ItemStack getItem() {
        return item;
    }

    public boolean canStore() {
        return canStore;
    }

    public boolean canDrop() {
        return canDrop;
    }

    public boolean canLoseOnDeath() {
        return canLoseOnDeath;
    }

    public boolean canLoseItemOnHeroLoss() {
        return loseItemOnHeroLoss;
    }

    public boolean dropsOnDeath() { return dropsOnDeath; }
}
