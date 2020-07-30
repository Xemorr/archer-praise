package me.xemor.archerpraise;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class ArcherPraiseCMD implements CommandExecutor, TabExecutor {

    String[] subCommands = {"reload"};
    ArcherPraise archerPraise;

    public ArcherPraiseCMD(ArcherPraise archerPraise) {
        this.archerPraise = archerPraise;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args == null) {
            sendHelpMessage(sender);
            return true;
        }
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                if (sender.hasPermission("archerpraise.reload")) {
                    archerPraise.loadConfigValues();
                    sender.sendMessage(ChatColor.GRAY + "Reloaded successfully!");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "You have insufficient permissions");
                }
                return true;
            }
        }
        sendHelpMessage(sender);
        return true;
    }

    public void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "-ARCHER PRAISE HELP-");
        sender.sendMessage(ChatColor.GRAY + "/archerpraise reload");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0])) {
                    list.add(subCommand);
                }
            }
        }
        return list;
    }
}
