package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.ItemComparisonData;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;

import java.util.List;

public class ThrowerData extends CooldownData {

    @JsonPropertyWithDefault
    private int ammoCost = 1;
    @JsonPropertyWithDefault
    @JsonAlias("item")
    private ItemComparisonData ammo = new ItemComparisonData();
    @JsonPropertyWithDefault
    private EntityType projectile = EntityType.SNOWBALL;
    @JsonPropertyWithDefault
    private List<Action> actions = List.of(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
    @JsonPropertyWithDefault
    private boolean canPickUp = false;
    @JsonPropertyWithDefault
    private double velocity = 1.4;
    @JsonPropertyWithDefault
    private double damage = 3;

    public int getAmmoCost() {
        return ammoCost;
    }

    public ItemComparisonData getAmmo() {
        return ammo;
    }

    public EntityType getProjectile() {
        return projectile;
    }

    public List<Action> getActions() {
        if (actions.isEmpty()) {
            actions.add(Action.RIGHT_CLICK_AIR);
            actions.add(Action.RIGHT_CLICK_BLOCK);
        }
        return actions;
    }

    public AbstractArrow.PickupStatus canPickUp() {
        return canPickUp ? AbstractArrow.PickupStatus.ALLOWED : AbstractArrow.PickupStatus.CREATIVE_ONLY;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getDamage() {
        return damage;
    }

}
