package me.invis.bukkittaskmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;

public class MainCommand implements CommandExecutor {

    private final String[] COMMAND_USAGE = new String[] {
            ChatColor.GOLD + "/task list" + ChatColor.RESET + " - Lists all running tasks.",
            ChatColor.GOLD + "/task list <plugin>" + ChatColor.RESET + " - Lists all tasks of a plugin.",
            ChatColor.GOLD + "/task stop <ID>" + ChatColor.RESET + " - Stops a task.",
            ChatColor.GOLD + "/task forceStop <ID>" + ChatColor.RESET + " - Forcefully stops a task.",
            ChatColor.GOLD + "/task owner <ID>" + ChatColor.RESET + " - Shows the owner(plugin) of a task.",
            ChatColor.GOLD + "/task stopAll <plugin>" + ChatColor.RESET + " - Stops all tasks of a plugin.",
            ChatColor.GOLD + "/task forceStopAll <plugin>" + ChatColor.RESET + " - Forcefully stops all tasks of a plugin.",
            ChatColor.GOLD + "/task panic" + ChatColor.RESET + " - Stops all tasks.",
            ChatColor.GOLD + "/task forcePanic" + ChatColor.RESET + " - Forcefully stops all tasks.",
    };

    private final String WRONG_USAGE = ChatColor.RED + "Wrong usage!, please see /task help.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sender.sendMessage(COMMAND_USAGE);
            return true;
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        List<BukkitTask> currentTasks = scheduler.getPendingTasks();
        switch (args[0].toLowerCase()) {
            case "list":
                if(args.length == 1) {
                    sender.sendMessage(ChatColor.GOLD + "Currently running tasks:" + ChatColor.RESET + " (" + currentTasks.size() + ")");
                    currentTasks.forEach(task -> sender.sendMessage(task.getTaskId() + " | " + task.getOwner().getName()));
                    break;
                }
                else if(args.length == 2){
                    sender.sendMessage(ChatColor.GOLD + "Currently running tasks:");
                    currentTasks.stream()
                            .filter(task -> task.getOwner().equals(Bukkit.getPluginManager().getPlugin(args[1])))
                            .forEach(task -> sender.sendMessage(task.getTaskId() + " | " + task.getOwner().getName()));
                    break;
                }
                else {
                    sender.sendMessage(WRONG_USAGE);
                    break;
                }
            case "stop":
                if(args.length != 2) {
                    sender.sendMessage(WRONG_USAGE);
                    break;
                }

                int id = parseInt(sender, args[1]);
                currentTasks.stream().filter(task -> task.getTaskId() == id).forEach(BukkitTask::cancel);
                sender.sendMessage(ChatColor.GOLD + "Cancelled task with ID: " + id);
                break;
            case "forcestop":
                if(args.length != 2) {
                    sender.sendMessage();
                    break;
                }

                id = parseInt(sender, args[1]);
                scheduler.cancelTask(id);
                sender.sendMessage(ChatColor.GOLD + "Forcefully cancelled task with ID: " + id);
                break;
            case "owner":
                if(args.length != 2) {
                    sender.sendMessage(WRONG_USAGE);
                    break;
                }

                id = parseInt(sender, args[1]);
                BukkitTask task = currentTasks.stream().filter(t -> t.getTaskId() == id).findFirst().orElse(null);
                if(task == null) {
                    sender.sendMessage(ChatColor.RED + "This task does not exist!");
                    return true;
                }

                sender.sendMessage(ChatColor.GOLD + "Task with ID " + id + " belongs to: " + ChatColor.RESET + task.getOwner().getName());
                break;
            case "stopall":
                if(args.length != 2) {
                    sender.sendMessage(WRONG_USAGE);
                    break;
                }

                Plugin selectedPlugin = Bukkit.getPluginManager().getPlugin(args[1]);
                if(selectedPlugin == null) {
                    sender.sendMessage(ChatColor.RED + "This plugin does not exist!");
                    return true;
                }
                currentTasks.stream().filter(task1 -> task1.getOwner().equals(selectedPlugin)).forEach(BukkitTask::cancel);
                sender.sendMessage(ChatColor.GOLD + "Cancelled all tasks of plugin: " + args[1]);
                break;
            case "forcestopall":
                if(args.length != 2) {
                    sender.sendMessage(WRONG_USAGE);
                    break;
                }

                Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
                if(plugin == null) {
                    sender.sendMessage(ChatColor.RED + "This plugin does not exist!");
                    return true;
                }
                scheduler.cancelTasks(plugin);
                sender.sendMessage(ChatColor.GOLD + "Cancelled all tasks of plugin: " + args[1]);
                break;
            case "panic":
                currentTasks.forEach(BukkitTask::cancel);
                sender.sendMessage(ChatColor.GOLD + "Cancelled all tasks.");
                break;
            case "forcepanic":
                Arrays.stream(Bukkit.getPluginManager().getPlugins()).forEach(scheduler::cancelTasks);
                sender.sendMessage(ChatColor.GOLD + "Forcefully cancelled all tasks.");
                break;
        }

        return true;
    }

    private int parseInt(CommandSender parser, String neutral) {
        try {
            return Integer.parseInt(neutral);
        }
        catch(NumberFormatException e) {
            parser.sendMessage(ChatColor.RED + "Please type a valid number!");
            return -1;
        }
    }
}
