package me.xemor.superheroes2.commands;


import de.themoep.minedown.adventure.MineDown;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.Superheroes2;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TextConvert implements SubCommand{

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(Superheroes2.getInstance().getConfigHandler().getTextConvertStatus())) {
            if (sender.hasPermission("superheroes.textconvert")) {
                File file = Superheroes2.getInstance().getConfigHandler().getDataFolder();
                sender.sendMessage(ChatColor.GREEN + "Starting text conversion");
                try {
                    fixFiles(file);
                    sender.sendMessage(ChatColor.GREEN + "Text converted.");
                    Superheroes2.getInstance().getConfigHandler().setTextConvertComplete();
                } catch (IOException e) {
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "IO Exception, Check console");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Your configs have already been converted to MiniMessage format. Change the textconvert value in the config to run this again. Running this command multiple times will escape all MiniMessage tags in <this> format. This will stop your configs working.");
        }

    }
    private void fixFiles(File file) throws IOException{
        File[] files = file.listFiles();
        for(File checkFile: files) {
            if (checkFile.isDirectory()) {
                fixFiles(checkFile);
            }
            else {
                convertText(checkFile);
            }
        }
    }

    private void convertText(File file) throws IOException {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        var set = yamlConfiguration.getValues(true).entrySet();
        for (Map.Entry<String, Object> entry : set) {
            if (entry.getValue() instanceof String string) {
                entry.setValue(convertString(string));
            } else {
                List<String> convertedStringList = yamlConfiguration.getStringList(entry.getKey()).stream().map(TextConvert::convertString).toList();
                if (!(convertedStringList.isEmpty())){
                    entry.setValue(convertedStringList);
                }
            }
            yamlConfiguration.set(entry.getKey(),entry.getValue());
        }
        yamlConfiguration.save(file);
    }

    private static String convertString(String string) {
        string = MiniMessage.miniMessage().serialize(new MineDown(string).toComponent());
        boolean opened = false;
        char[] array = string.toCharArray();
        for(int i = 0; i < array.length; i++) {
            if(array[i] == '%') {
                if(opened) {
                    array[i] = '>';
                } else {
                    array[i] = '<';
                }
                opened = !opened;
            }
        }
        return String.valueOf(array);
    }



    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
