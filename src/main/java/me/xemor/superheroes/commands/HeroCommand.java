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
    private ConfigHandler configHandler;
    private SelectCommand selectCommand;
    private ReloadCommand reloadCommand;
    private ImportCommand importCommand;
    private ExportCommand exportCommand;
    private GUICommand guiCommand;
    private CheckCommand checkCommand;
    private RerollCommand rerollCommand;

    public HeroCommand(HeroHandler heroHandler) {
        this.rerollCommand = new RerollCommand();
        this.configHandler = heroHandler.getPlugin().getConfigHandler();
        this.reloadCommand = new ReloadCommand(heroHandler, this.configHandler);
        this.selectCommand = new SelectCommand(heroHandler, this.configHandler);
        this.importCommand = new ImportCommand();
        this.exportCommand = new ExportCommand();
        this.guiCommand = new GUICommand();
        this.checkCommand = new CheckCommand();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            SubCommands commandType;
            Audience audience = Superheroes.getBukkitAudiences().sender(sender);
            try {
                commandType = SubCommands.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                audience.sendMessage(MiniMessage.miniMessage().deserialize(this.configHandler.getInvalidCommandMessage(), Placeholder.unparsed("player", sender.getName())));
                return true;
            }
            switch (commandType) {
                case SELECT: {
                    this.selectCommand.onCommand(sender, args);
                    break;
                }
                case RELOAD: {
                    this.reloadCommand.onCommand(sender, args);
                    break;
                }
                case REROLL: {
                    this.rerollCommand.onCommand(sender, args);
                    break;
                }
                case EXPORT: {
                    this.exportCommand.onCommand(sender, args);
                    break;
                }
                case IMPORT: {
                    this.importCommand.onCommand(sender, args);
                    break;
                }
                case CHECK: {
                    this.checkCommand.onCommand(sender, args);
                    break;
                }
                case GUI: {
                    this.guiCommand.onCommand(sender, args);
                }
            }
        }
        return true;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabComplete;
        block13:
        {
            block12:
            {
                tabComplete = new ArrayList<String>();
                if (args.length != 1) break block12;
                for (SubCommands subCommandEnum : SubCommands.values()) {
                    String subCommandStr = subCommandEnum.toString().toLowerCase();
                    if (!subCommandStr.startsWith(args[0].toLowerCase())) continue;
                    tabComplete.add(subCommandStr);
                }
                break block13;
            }
            if (args.length <= 1) break block13;
            try {
                SubCommands subCommand = SubCommands.valueOf(args[0].toUpperCase());
                tabComplete = switch (subCommand) {
                    default -> throw new IncompatibleClassChangeError();
                    case SELECT -> this.selectCommand.tabComplete(sender, args);
                    case RELOAD -> this.reloadCommand.tabComplete(sender, args);
                    case REROLL -> this.rerollCommand.tabComplete(sender, args);
                    case EXPORT -> this.exportCommand.tabComplete(sender, args);
                    case IMPORT -> this.importCommand.tabComplete(sender, args);
                    case CHECK -> this.checkCommand.tabComplete(sender, args);
                    case GUI -> this.guiCommand.tabComplete(sender, args);
                };
            } catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return tabComplete;
    }
}

