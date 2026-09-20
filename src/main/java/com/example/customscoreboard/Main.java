package com.example.customscoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        if (getCommand("customboard") != null) {
            getCommand("customboard").setExecutor(this);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        setScoreboard(event.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("customscoreboard.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            reloadConfig();

            for (Player player : Bukkit.getOnlinePlayers()) {
                setScoreboard(player);
            }

            sender.sendMessage(ChatColor.GREEN + "Scoreboard configuration reloaded successfully!");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " reload");
        return true;
    }

    public void setScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("CustomBoard", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        String title = getConfig().getString("scoreboard.title", "&e&lServer");
        obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));

        List<String> lines = getConfig().getStringList("scoreboard.lines");
        int scoreIndex = lines.size();

        for (String line : lines) {
            line = line.replace("%player%", player.getName());

            if (line.isEmpty()) {
                line = String.format("%" + scoreIndex + "s", "");
            }

            Score score = obj.getScore(ChatColor.translateAlternateColorCodes('&', line));
            score.setScore(scoreIndex);
            scoreIndex--;
        }

        player.setScoreboard(board);
    }
}
