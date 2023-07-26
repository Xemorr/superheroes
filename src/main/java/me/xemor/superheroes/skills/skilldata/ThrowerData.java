package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.comparison.ItemComparisonData;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;

import java.util.List;
import java.util.stream.Collectors;

public class ThrowerData extends CooldownData {

    private final int ammoCost;
    private ItemComparisonData ammo;
    private final EntityType entityType;
    private final List<Action> actions;
    private final AbstractArrow.PickupStatus canPickUp;
    private final double velocity;
    private final double damage;

    public ThrowerData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection, "<grey>Your projectile has <s> seconds remaining!", 0);
        ammoCost = configurationSection.getInt("ammoCost", 1);
        final ConfigurationSection ammoItemSection = configurationSection.getConfigurationSection("item");
        if (ammoItemSection != null) {
            ammo = new ItemComparisonData(ammoItemSection);
        }
        entityType = EntityType.valueOf(configurationSection.getString("projectile", "SNOWBALL").toUpperCase());
        actions = configurationSection.getStringList("actions").stream().map(Action::valueOf).collect(Collectors.toList());
        if (actions.isEmpty()) {
            actions.add(Action.RIGHT_CLICK_AIR);
            actions.add(Action.RIGHT_CLICK_BLOCK);
        }
        canPickUp = configurationSection.getBoolean("canPickUp", false) ? AbstractArrow.PickupStatus.ALLOWED : AbstractArrow.PickupStatus.CREATIVE_ONLY;
        velocity = configurationSection.getDouble("velocity", 1.4);
        damage = configurationSection.getDouble("damage", 3);
    }

    public int getAmmoCost() {
        return ammoCost;
    }

    public ItemComparisonData getAmmo() {
        return ammo;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public List<Action> getActions() {
        return actions;
    }

    public AbstractArrow.PickupStatus canPickUp() {
        return canPickUp;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getDamage() {
        return damage;
    }

}
