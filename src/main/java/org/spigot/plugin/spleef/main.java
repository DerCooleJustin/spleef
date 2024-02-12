/*
 * Copyright (c) 2024.
 * IF this plugin was free:
 * 1. You do have permission to publish it free of charge.
 * 1.1 IF you do, you have to mention me atleast once in your post.
 * 2. You do have permission to publish it with charge, IF you performed at least 3 major changes to the plugin.
 * 2.1 This does not mean you can charge other individuals for changing 3 Strings and/or messages.
 * 2.2 This DOES mean that you have to change AT LEAST one way the plugin handles teleportation.
 * 2.3 IF you publish your version of the plugin with charge, you HAVE to link the original at LEAST once where users can see it.
 * 2.4 IF you modify this plugin to behave malicious, I WILL perform legal steps against you.
 * 2.4.1 This ALSO applies to any other behavior that the description of the modified version does not comply with, AS WELL as intentionally coded bugs and/or errors that might break the server, the world, the players and/or performs any other type of damage to the server and/or users/moderators/admins/... .
 * 3. You have permission to crack, reverse engineer and any other type of looking into the code and the plugin's performance/behavior.
 * 4. If you find any security risk and/or other privace errors, you do have permission to modify the original plugin and send me your version, along with explanation and steps the reproduce the issue. I will look over it and try to release a patch. This patch might be, include or include parts of your version.
 */

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
    public static final Location spleefSpawn = new Location( //TODO: Fix coords and rotation
            Bukkit.getWorld(worldName),
            0,
            0,
            0,
            0,
            0
    );

    public static final Location viewerPlatform = new Location( //TODO: Fix coords and rotation
            Bukkit.getWorld(worldName),
            0,
            0,
            0,
            0,
            0
    );
    public static class SpleefStarts /*TODO: Fix coords and rotation*/ {
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
