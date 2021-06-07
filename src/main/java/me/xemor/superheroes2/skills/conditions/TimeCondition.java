package me.xemor.superheroes2.skills.conditions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class TimeCondition extends Condition implements HeroCondition {

    private final long minimumTime;
    private final long maximumTime;

    public TimeCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        minimumTime = configurationSection.getLong("minimumTime", 0);
        maximumTime = configurationSection.getLong("maximumTime", 24000);
    }


    @Override
    public boolean isTrue(Player player) {
        long time = player.getWorld().getTime();
        return  time >= minimumTime && time <= maximumTime;
    }
}
