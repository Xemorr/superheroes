package me.xemor.superheroes2.commands;

import me.xemor.superheroes2.CooldownHandler;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.ConfigHandler;
import me.xemor.superheroes2.data.HeroHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Reroll implements SubCommand, Listener {

    HeroHandler heroHandler;
    ConfigHandler configHandler;
    boolean isEnabled;
    private final Component noPermission = MiniMessage.miniMessage().deserialize("<dark_red>You do not have permission to use this power!");
    CooldownHandler cooldownHandler = new CooldownHandler("", ChatMessageType.ACTION_BAR);

    public Reroll(HeroHandler heroHandler, ConfigHandler configHandler) {
        this.heroHandler = heroHandler;
        this.configHandler = configHandler;
        isEnabled = configHandler.isRerollEnabled();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (isEnabled) {
                if (configHandler.getRerollItem().matches(item)) {
                    if (cooldownHandler.isCooldownOver(e.getPlayer().getUniqueId())) {
                        item.setAmount(item.getAmount() - 1);
                        heroHandler.setRandomHero(player);
                        cooldownHandler.startCooldown(configHandler.getRerollCooldown(), player.getUniqueId());
                    }
                }
            }
        }
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = Superheroes2.getBukkitAudiences().sender(sender);
        if (sender.hasPermission("superheroes.reroll")) {
            Player player;
            if (args.length >= 2) {
                player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    audience.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.getInvalidPlayerMessage(), Placeholder.unparsed("player", sender.getName())));
                    return;
                }
            }
            else {
                if (sender instanceof Player) {
                    player = (Player) sender;
                }
                else {
                    return;
                }
            }
            heroHandler.setRandomHero(player);
        }
        else {
            audience.sendMessage(noPermission);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
