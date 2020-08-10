package me.xemor.archerpraise;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class ArcherPraise extends JavaPlugin implements Listener {

    private static String message;
    private static int distanceThreshold;
    private static int cooldown;
    private CooldownHandler cooldownHandler = new CooldownHandler("");
    private static boolean notifyAll;
    private static List<String> commands;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        PluginCommand archerPraiseCommand = this.getCommand("archerpraise");
        ArcherPraiseCMD archerPraiseCMD = new ArcherPraiseCMD(this);
        archerPraiseCommand.setExecutor(archerPraiseCMD);
        archerPraiseCommand.setTabCompleter(archerPraiseCMD);
        saveDefaultConfig();
        loadConfigValues();
    }

    public void loadConfigValues() {
        reloadConfig();
        FileConfiguration config = getConfig();
        message = ChatColor.translateAlternateColorCodes('&', config.getString("message"));
        distanceThreshold = config.getInt("distanceThreshold");
        notifyAll = config.getBoolean("notifyAll");
        commands = config.getStringList("commandsToRun");
        cooldown = config.getInt("cooldown");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                if (cooldownHandler.isCooldownOver(player.getUniqueId())) {
                    int distance = (int) getLocationShotFrom(arrow).distance(e.getEntity().getLocation());
                    if (distance >= distanceThreshold) {
                        String tempMessage = message.replaceAll("%player%", player.getName());
                        tempMessage = tempMessage.replaceAll("%distance%", String.valueOf(distance));
                        if (notifyAll) {
                            Bukkit.broadcastMessage(tempMessage);
                        }
                        else {
                            player.sendMessage(tempMessage);
                        }
                        executeCommands(player);
                        cooldownHandler.startCooldown(cooldown, player.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArrowShoot(ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof Arrow) {
            Location location = e.getLocation();
            e.getEntity().setMetadata("ArcherPraiseLocation", new FixedMetadataValue(this, location));
        }
    }

    public Location getLocationShotFrom(Arrow arrow) {
        return (Location) arrow.getMetadata("ArcherPraiseLocation").get(0).value();
    }

    public void executeCommands(Player player) {
        for (String string : commands) {
            string = string.replaceAll("%player%", player.getName());
            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string);
            }catch(CommandException e) {
                e.printStackTrace();
            }
        }
    }


}
