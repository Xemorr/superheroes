package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.ItemStackData;
import org.bukkit.configuration.ConfigurationSection;

public class GiveItemData extends SkillData {

    private ItemStackData item;
    private boolean canStore;
    private boolean canDrop;
    private boolean canLoseOnDeath;
    private boolean loseItemOnHeroLoss;

    public GiveItemData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        canStore = configurationSection.getBoolean("canStore", true);
        canDrop = configurationSection.getBoolean("canDrop", true);
        canLoseOnDeath = configurationSection.getBoolean("canLoseOnDeath", true);
        loseItemOnHeroLoss = configurationSection.getBoolean("loseItemOnHeroLoss", true);
        ConfigurationSection itemConfig = configurationSection.getConfigurationSection("item");
        itemConfig = itemConfig == null ? configurationSection : itemConfig;
        ItemStackData itemStackData = new ItemStackData(itemConfig);
        item = itemStackData;
    }

    public ItemStackData getItemStackData() {
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
}
