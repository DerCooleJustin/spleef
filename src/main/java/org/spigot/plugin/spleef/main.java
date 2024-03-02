package org.spigot.plugin.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class main extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getLogger().info(getPrefix(true));
        Bukkit.getLogger().info(getPrefix(true) + "****The plugin is starting!****");
        Bukkit.getLogger().info(getPrefix(true) + "Checking conditions............");
        if (Bukkit.getWorld(worldName) == null) {
            Bukkit.getLogger().severe(getPrefix(true) + "ERROR! The world has not been found. Please make sure the spleef world is called \"" + worldName + "\". If it isn't, rename the world and reboot the server.");
            Bukkit.getLogger().warning(getPrefix(true) + "This plugin is now shutting down.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            Bukkit.getLogger().info(getPrefix(true) + "SUCCESS!.......................");
        }
        Bukkit.getLogger().info(getPrefix(true) + "Adding Listener................");
        new spleefListener(this);
        Bukkit.getLogger().info(getPrefix(true) + "Registering command............");
        Objects.requireNonNull(getCommand("spleef")).setExecutor(new spleefCommand());
        Bukkit.getLogger().info(getPrefix(true) + "*************DONE!*************");
        Bukkit.getLogger().info(getPrefix(true));
    }

    public static String getPrefix(boolean consoleVersion){
        if (consoleVersion) {
            return "[SPLEEF] ";
        } else {
            return "§8§l[§3§lSPL§2§lEEF§8§l]§r ";
        }
    }
    public static final String worldName = "spleef"; /*TODO: Weltname eintragen*/

    public static class locations /*TODO: Fix coords and rotation*/ {
        final Location spleefSpawn = new Location( //TODO: Fix coords and rotation
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
        final Location viewerPlatform = new Location( //TODO: Fix coords and rotation
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );

        final Location ONE = new Location(
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
        final Location TWO = new Location(
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
        final Location THREE = new Location(
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
        final Location FOUR = new Location(
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
        final Location FIVE = new Location(
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
        final Location SIX = new Location(
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
        final Location SEVEN = new Location(
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
        final Location EIGHT = new Location(
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
        final Location NINE = new Location(
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
        final Location TEN = new Location(
                Bukkit.getWorld(worldName),
                0,
                0,
                0,
                0,
                0
        );
    }
}
