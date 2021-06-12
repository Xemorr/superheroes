package me.xemor.superheroes2.commands;

import de.themoep.minedown.adventure.MineDown;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.data.ConfigHandler;
import me.xemor.superheroes2.data.HeroHandler;
import net.kyori.adventure.audience.Audience;
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
    private HeroSelect heroSelectCommand;
    private Reload reloadCommand;
    private Import importCommand;
    private Export exportCommand;
    private Reroll reroll;

    public HeroCommand(HeroHandler heroHandler, Reroll reroll) {
        this.heroHandler = heroHandler;
        this.reroll = reroll;
        configHandler = heroHandler.getPlugin().getConfigHandler();
        reloadCommand = new Reload(heroHandler, configHandler);
        heroSelectCommand = new HeroSelect(heroHandler, configHandler);
        importCommand = new Import(heroHandler, configHandler);
        exportCommand = new Export(heroHandler, configHandler);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            Audience audience = Superheroes2.getBukkitAudiences().sender(sender);
            SubCommands commandType;
            try {
                commandType = SubCommands.valueOf(args[0].toUpperCase());
            } catch(IllegalArgumentException e) {
                audience.sendMessage(MineDown.parse(configHandler.getInvalidCommandMessage(), "player", sender.getName()));
                return true;
            }
            switch (commandType) {
                case SELECT: heroSelectCommand.onCommand(sender, args); break;
                case RELOAD: reloadCommand.onCommand(sender, args); break;
                case REROLL: reroll.onCommand(sender, args); break;
                case EXPORT: exportCommand.onCommand(sender, args); break;
                case IMPORT: importCommand.onCommand(sender, args); break;
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
                switch (subCommand) {
                    case SELECT: tabComplete = heroSelectCommand.tabComplete(sender, args); break;
                    case RELOAD: tabComplete = reloadCommand.tabComplete(sender, args); break;
                    case REROLL: tabComplete = reroll.tabComplete(sender, args); break;
                    case EXPORT: tabComplete = exportCommand.tabComplete(sender, args); break;
                    case IMPORT: tabComplete = importCommand.tabComplete(sender, args); break;
                }
            } catch(IllegalArgumentException ignored) {}
        }
        return tabComplete;
    }
}
