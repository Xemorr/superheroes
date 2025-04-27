package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class DamageModifierData extends SkillData {

    @JsonPropertyWithDefault
    private SetData<EntityType> entities = new SetData<>();
    @JsonPropertyWithDefault
    private SetData<EntityDamageEvent.DamageCause> causes = new SetData<>();
    @JsonPropertyWithDefault
    private boolean whitelist = false;
    @JsonPropertyWithDefault
    private double expectedMaxDamage = 30;
    @JsonPropertyWithDefault
    private double maxDamage = 15;
    @JsonPropertyWithDefault
    private double minDamage = 0;
    @JsonPropertyWithDefault
    private int priority = 0;
    @JsonPropertyWithDefault
    private boolean incoming = false;
    @JsonPropertyWithDefault
    private boolean outgoing = false;
    @JsonPropertyWithDefault
    private boolean eased = false;
    @JsonPropertyWithDefault
    private boolean limitProjectiles = true;

    public boolean isOutgoing() {
        return outgoing;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public int getPriority() {
        return priority;
    }

    public boolean limitProjectiles() {
        return limitProjectiles;
    }

    public boolean isValidCause(EntityDamageEvent.DamageCause damageCause) {
        // blacklist nonempty and it is NOT in it
        // whitelist nonempty and it is IN it
        // both are empty
        boolean in = causes.getSet().contains(damageCause);
        if (!whitelist) {
            in = !in;
        };
        return in;
    }

    public boolean isValidEntity(EntityType entityType) {
        // blacklist nonempty and it is NOT in it
        // whitelist nonempty and it is IN it
        // both are empty
        boolean in = entities.getSet().contains(entityType);
        if (!whitelist) {
            in = !in;
        };
        return in;
    }

    public double calculateDamage(double damage) {
        if (damage < minDamage) {
            damage = minDamage;
        }

        if (eased) {
            return damage > expectedMaxDamage ? maxDamage : maxDamage * (1 - (1 - damage / expectedMaxDamage) * (1 - damage / expectedMaxDamage));
        } else {
            return Math.min(Math.max(damage, minDamage), maxDamage);
        }
    }
}
