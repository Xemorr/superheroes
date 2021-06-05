package me.xemor.superheroes2.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    void onCommand(CommandSender sender, String[] args);
    List<String> tabComplete(CommandSender sender, String[] args);

}
