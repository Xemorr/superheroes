package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.ItemStackData;
import org.bukkit.configuration.ConfigurationSection;

public class GiveItemData extends SkillData {

    private final ItemStackData item;
    private final boolean canStore;
    private final boolean canDrop;
    private final boolean canLoseOnDeath;
    private final boolean loseItemOnHeroLoss;
    private final boolean dropsOnDeath;

    public GiveItemData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        canStore = configurationSection.getBoolean("canStore", true);
        canDrop = configurationSection.getBoolean("canDrop", true);
        dropsOnDeath = configurationSection.getBoolean("canDropOnDeath", true);
        canLoseOnDeath = configurationSection.getBoolean("canLoseOnDeath", true);
        loseItemOnHeroLoss = configurationSection.getBoolean("loseItemOnHeroLoss", true);
        ConfigurationSection itemConfig = configurationSection.getConfigurationSection("item");
        itemConfig = itemConfig == null ? configurationSection : itemConfig;
        item = new ItemStackData(itemConfig);
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

    public boolean dropsOnDeath() { return dropsOnDeath; }
}
