package me.xemor.superheroes.commands;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.ConfigHandler;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.data.SuperheroPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HeroSelect implements SubCommand {

    private final HeroHandler heroHandler;
    private final ConfigHandler configHandler;

    public HeroSelect(HeroHandler heroHandler, ConfigHandler configHandler) {
        this.heroHandler = heroHandler;
        this.configHandler = configHandler;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = Superheroes.getBukkitAudiences().sender(sender);
        if (!sender.hasPermission("superheroes.hero")) {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.getNoPermissionMessage(), Placeholder.unparsed("player", sender.getName())));
            return;
        }
        if (args.length <= 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Superhero superhero = heroHandler.getSuperhero(player);
                audience.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.getCurrentHeroMessage(),
                                Placeholder.unparsed("player", player.getName()),
                                Placeholder.unparsed("hero", superhero.getName())));
            }
            return;
        }
        Superhero power = heroHandler.getSuperhero(args[1].toLowerCase());
        if (power == null) {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.getInvalidHeroMessage(),
                    Placeholder.unparsed("player", sender.getName()),
                    Placeholder.unparsed("hero", args[1])
                    ));
            return;
        }
        if (!sender.hasPermission("superheroes.hero." + power.getName().toLowerCase()) && configHandler.areHeroPermissionsRequired()) {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.getNoPermissionMessage(), Placeholder.unparsed("player", sender.getName())));
            return;
        }
        if (!sender.hasPermission("superheroes.hero.bypasscooldown") && sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            SuperheroPlayer superheroPlayer = heroHandler.getSuperheroPlayer(senderPlayer);
            long seconds = getCooldownLeft(superheroPlayer) / 1000;
            if (!isCooldownOver(superheroPlayer)) {
                Component message = MiniMessage.miniMessage().deserialize(configHandler.getHeroCooldownMessage(),
                        Placeholder.unparsed("player", senderPlayer.getDisplayName()),
                        Placeholder.unparsed("currentcooldown", String.valueOf(Math.round(seconds))),
                        Placeholder.unparsed("cooldown", String.valueOf(configHandler.getHeroCommandCooldown())));
                audience.sendMessage(message);
                return;
            }
        }
        Player player;
        if (args.length >= 3 && sender.hasPermission("superheroes.hero.others")) {
            player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                MiniMessage.miniMessage().deserialize(configHandler.getInvalidPlayerMessage(), Placeholder.unparsed("player", sender.getName()));
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
        heroHandler.setHero(player, power);
        SuperheroPlayer superheroPlayer = heroHandler.getSuperheroPlayer(player);
        heroHandler.getHeroIOHandler().saveSuperheroPlayerAsync(superheroPlayer);
        if (!sender.hasPermission("superheroes.hero.bypasscooldown") && sender instanceof Player && sender == player) {
            superheroPlayer.setHeroCommandTimestamp(System.currentTimeMillis());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> heroesTabComplete = new ArrayList<>();
        if (args.length == 2) {
            String secondArg = args[1];
            for (Superhero superhero : heroHandler.getNameToSuperhero().values()) {
                if (superhero.getName().startsWith(secondArg) && sender.hasPermission("superheroes.hero." + superhero.getName().toLowerCase())) {
                    heroesTabComplete.add(superhero.getName());
                }
            }
            return heroesTabComplete;
        }
        return null;
    }

    public boolean isCooldownOver(SuperheroPlayer superheroPlayer) {
        return getCooldownLeft(superheroPlayer) <= 0;
    }

    public long getCooldownLeft(SuperheroPlayer superheroPlayer) {
        long cooldown = configHandler.getHeroCommandCooldown() * 1000;
        return cooldown - (System.currentTimeMillis() - superheroPlayer.getHeroCommandTimestamp());
    }

}
