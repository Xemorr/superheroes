package me.xemor.superheroes.commands;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.ConfigHandler;
import me.xemor.superheroes.data.HeroHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HeroCommand implements CommandExecutor, TabExecutor {
    private final ConfigHandler configHandler;
    private final SelectCommand selectCommand;
    private final ReloadCommand reloadCommand;
    private final ImportCommand importCommand;
    private final ExportCommand exportCommand;
    private final GUICommand guiCommand;
    private final CheckCommand checkCommand;
    private final RerollCommand rerollCommand;
    private final RemoveAttributesCommand removeAttributesCommand;

    public HeroCommand(HeroHandler heroHandler) {
        this.rerollCommand = new RerollCommand();
        this.configHandler = heroHandler.getPlugin().getConfigHandler();
        this.reloadCommand = new ReloadCommand(heroHandler, this.configHandler);
        this.selectCommand = new SelectCommand(heroHandler, this.configHandler);
        this.importCommand = new ImportCommand();
        this.exportCommand = new ExportCommand();
        this.guiCommand = new GUICommand();
        this.checkCommand = new CheckCommand();
        this.removeAttributesCommand = new RemoveAttributesCommand();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Audience audience = Superheroes.getBukkitAudiences().sender(sender);
        if (!sender.hasPermission("superheroes.hero")) {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(this.configHandler.getNoPermissionMessage()));
            return true;
        }
        if (args.length >= 1) {
            SubCommands commandType;
            try {
                commandType = SubCommands.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                audience.sendMessage(MiniMessage.miniMessage().deserialize(this.configHandler.getInvalidCommandMessage(), Placeholder.unparsed("player", sender.getName())));
                return true;
            }
            switch (commandType) {
                case SELECT -> this.selectCommand.onCommand(sender, args);
                case RELOAD -> this.reloadCommand.onCommand(sender, args);
                case REROLL -> this.rerollCommand.onCommand(sender, args);
                case EXPORT -> this.exportCommand.onCommand(sender, args);
                case IMPORT -> this.importCommand.onCommand(sender, args);
                case CHECK -> this.checkCommand.onCommand(sender, args);
                case REMOVEATTRIBUTES -> this.removeAttributesCommand.onCommand(sender, args);
                case GUI -> this.guiCommand.onCommand(sender, args);
            }
        }
        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabComplete = new ArrayList<>();
        if (args.length == 1) {
            for (SubCommands subCommandEnum : SubCommands.values()) {
                String subCommandStr = subCommandEnum.toString().toLowerCase();
                if (subCommandStr.startsWith(args[0].toLowerCase())) {
                    tabComplete.add(subCommandStr);
                }
            }
        }
        else if (args.length > 1) {
            SubCommands subCommand;
            try {
                subCommand = SubCommands.valueOf(args[0].toUpperCase());
                tabComplete = switch (subCommand) {
                    case SELECT -> selectCommand.tabComplete(sender, args);
                    case RELOAD -> reloadCommand.tabComplete(sender, args);
                    case REROLL -> rerollCommand.tabComplete(sender, args);
                    case EXPORT -> exportCommand.tabComplete(sender, args);
                    case IMPORT -> importCommand.tabComplete(sender, args);
                    case CHECK -> checkCommand.tabComplete(sender, args);
                    case GUI -> guiCommand.tabComplete(sender, args);
                    case REMOVEATTRIBUTES -> removeAttributesCommand.tabComplete(sender, args);
                };
            } catch(IllegalArgumentException ignored) {}
        }
        return tabComplete;
    }
}

