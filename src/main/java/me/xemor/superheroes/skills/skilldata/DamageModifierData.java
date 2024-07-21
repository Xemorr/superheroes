package me.xemor.superheroes.skills.skilldata;

import me.xemor.superheroes.configurationdata.comparison.SetData;
import me.xemor.superheroes.org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageModifierData extends SkillData {

    private final SetData<EntityType> entities;

    private final SetData<EntityDamageEvent.DamageCause> causes;

    private final boolean whitelist;

    private final double expectedMaxDamage;

    private final double maxDamage;

    private final double minDamage;
    private final int priority;
    private final boolean incoming;
    private final boolean outgoing;
    private final boolean eased;

    public DamageModifierData(int skill, @NotNull ConfigurationSection configurationSection) {
        super(skill, configurationSection);

        expectedMaxDamage = configurationSection.getDouble("expectedMaxDamage", 30);
        maxDamage = configurationSection.getDouble("maxDamage", 15);
        minDamage = configurationSection.getDouble("minDamage", 0);
        causes= new SetData<>(EntityDamageEvent.DamageCause.class, "causes",
            configurationSection);
        entities = new SetData<>(EntityDamageEvent.DamageCause.class, "entities",
            configurationSection);

        whitelist = configurationSection.getBoolean("whitelist", false);


        incoming = configurationSection.getBoolean("incoming", false);
        outgoing = configurationSection.getBoolean("outgoing", false);

        eased = configurationSection.getBoolean("eased", false);
        priority = configurationSection.getInt("priority", 0);
    }

    public boolean isOutgoing() {
        return outgoing;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public int getPriority() {
        return priority;
    }

    public final boolean isValidCause(EntityDamageEvent.DamageCause damageCause) {
        // blacklist nonempty and it is NOT in it
        // whitelist nonempty and it is IN it
        // both are empty
        return (whitelist && !causes.inSet(damageCause));
    }

    public final boolean isValidEntity(EntityType entityType) {
        // blacklist nonempty and it is NOT in it
        // whitelist nonempty and it is IN it
        // both are empty
        return (whitelist && !entities.inSet(entityType));
    }

    public final double calculateDamage(double damage) {

        if (damage < minDamage) {
            damage = minDamage;
        }

        if (eased) {
            return damage > expectedMaxDamage ? maxDamage :
                maxDamage * (1 - (1 - damage / expectedMaxDamage) * (1 - damage / expectedMaxDamage));
        } else {
            return Math.min(Math.max(damage, minDamage), maxDamage);
        }
    }
}
