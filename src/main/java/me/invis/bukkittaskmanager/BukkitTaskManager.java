package me.invis.bukkittaskmanager;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class BukkitTaskManager extends JavaPlugin implements CommandExecutor {
    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("bukkittask")).setExecutor(new MainCommand());
    }
}
