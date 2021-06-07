package me.xemor.superheroes2.skills.conditions;

import me.xemor.superheroes2.CooldownHandler;
import me.xemor.superheroes2.skills.skilldata.exceptions.InvalidConfig;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

public class CooldownCondition extends Condition implements HeroCondition {

    private final CooldownHandler cooldownHandler;
    private final String cooldownMessage;
    private final double cooldown;

    public CooldownCondition(int condition, ConfigurationSection configurationSection) throws InvalidConfig {
        super(condition, configurationSection);
        cooldown = configurationSection.getDouble("cooldown", 10D);
        try {
            ChatMessageType chatMessageType = ChatMessageType.valueOf(configurationSection.getString("messagetype", "ACTION_BAR").toUpperCase(Locale.ROOT));
            cooldownHandler = new CooldownHandler("", chatMessageType);
        } catch(IllegalArgumentException e) {
            throw new InvalidConfig("Invalid Chat Message Type specified, must be CHAT or ACTION_BAR");
        }
        cooldownMessage = configurationSection.getString("cooldownMessage", "You must wait %currentcooldown% more seconds");
    }

    @Override
    public boolean isTrue(Player player) {
        UUID uuid = player.getUniqueId();
        boolean result = cooldownHandler.isCooldownOver(uuid, cooldownMessage);
        if (result) cooldownHandler.startCooldown(cooldown, uuid);
        return result;
    }
}
