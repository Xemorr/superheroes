package space.planetangus.damagecontrol.skilldata;

import me.xemor.superheroes.configurationdata.comparison.SetData;
import me.xemor.superheroes.org.jetbrains.annotations.NotNull;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageModifierData extends SkillData {

    private final SetData<EntityType> entitywhitelist;

    private final SetData<EntityType> entityblacklist;

    private final SetData<EntityDamageEvent.DamageCause> causeblacklist;

    private final SetData<EntityDamageEvent.DamageCause> causewhitelist;


    private final double expectedMaximumDamage;

    private final double damageCap;

    private final double damageMin;
    private final int priority;
    private final boolean incoming;
    private final boolean outgoing;
    private final boolean eased;

    public DamageModifierData(int skill,
                              @NotNull ConfigurationSection configurationSection) {
        super(skill, configurationSection);

        expectedMaximumDamage = configurationSection.getDouble("expectedMaxDamage", 30);
        damageCap = configurationSection.getDouble("damageMax", 15);
        damageMin = configurationSection.getDouble("damageMin", 0);
        causeblacklist =
            new SetData<>(EntityDamageEvent.DamageCause.class, "causeblacklist",
                configurationSection);
        causewhitelist =
            new SetData<>(EntityDamageEvent.DamageCause.class, "causewhitelist",
                configurationSection);

        entitywhitelist = new SetData<>(EntityType.class, "entityblacklist", configurationSection);
        entityblacklist = new SetData<>(EntityType.class, "entitywhitelist", configurationSection);

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
        return (!causeblacklist.getSet().isEmpty() && !causeblacklist.inSet(damageCause)) ||
            (!causewhitelist.getSet().isEmpty() && causewhitelist.inSet(damageCause)) ||
            (causewhitelist.getSet().isEmpty() && causeblacklist.getSet()
                .isEmpty());
    }

    public final boolean isValidEntity(EntityType entityType) {
        // blacklist nonempty and it is NOT in it
        // whitelist nonempty and it is IN it
        // both are empty
        return (!entityblacklist.getSet().isEmpty() && !entityblacklist.inSet(entityType)) ||
            (!entitywhitelist.getSet().isEmpty() && entitywhitelist.inSet(entityType)) ||
            (entitywhitelist.getSet().isEmpty() && entityblacklist.getSet()
                .isEmpty());
    }

    public final double calculateDamage(double damage) {
        double x = damage;

        if (x < damageMin) {
            x = damageMin;
        }

        if (eased) {
            return x > expectedMaximumDamage ? damageCap :
                damageCap * (1 - (1 - x / expectedMaximumDamage) * (1 - x / expectedMaximumDamage));
        } else {
            return Math.min(Math.max(x, damageMin), damageCap);
        }
    }
}
