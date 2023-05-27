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

    private HeroHandler heroHandler;
    private ConfigHandler configHandler;
    private Select selectCommand;
    private Reload reloadCommand;
    private Import importCommand;
    private Export exportCommand;
    private TextConvert textConvertCommand;

    private GUI guiCommand;

    private Check checkCommand;
    private Reroll reroll;

    public HeroCommand(HeroHandler heroHandler, Reroll reroll) {
        this.heroHandler = heroHandler;
        this.reroll = reroll;
        configHandler = heroHandler.getPlugin().getConfigHandler();
        reloadCommand = new Reload(heroHandler, configHandler);
        selectCommand = new Select(heroHandler, configHandler);
        importCommand = new Import();
        exportCommand = new Export();
        guiCommand = new GUI();
        textConvertCommand = new TextConvert();
        checkCommand = new Check();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            Audience audience = Superheroes.getBukkitAudiences().sender(sender);
            SubCommands commandType;
            try {
                commandType = SubCommands.valueOf(args[0].toUpperCase());
            } catch(IllegalArgumentException e) {
                audience.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.getInvalidCommandMessage(), Placeholder.unparsed("player", sender.getName())));
                return true;
            }
            switch (commandType) {
                case SELECT -> selectCommand.onCommand(sender, args);
                case RELOAD -> reloadCommand.onCommand(sender, args);
                case REROLL -> reroll.onCommand(sender, args);
                case EXPORT -> exportCommand.onCommand(sender, args);
                case IMPORT -> importCommand.onCommand(sender, args);
                case TEXTCONVERT -> textConvertCommand.onCommand(sender, args);
                case CHECK -> checkCommand.onCommand(sender, args);
                case GUI -> guiCommand.onCommand(sender, args);
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
                    case REROLL -> reroll.tabComplete(sender, args);
                    case EXPORT -> exportCommand.tabComplete(sender, args);
                    case IMPORT -> importCommand.tabComplete(sender, args);
                    case TEXTCONVERT -> textConvertCommand.tabComplete(sender, args);
                    case CHECK -> checkCommand.tabComplete(sender, args);
                    case GUI -> guiCommand.tabComplete(sender, args);
                };
            } catch(IllegalArgumentException ignored) {}
        }
        return tabComplete;
    }
}
