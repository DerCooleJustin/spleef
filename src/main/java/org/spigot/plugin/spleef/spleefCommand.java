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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class spleefCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String str, String[] str2) {
        if(sender instanceof Player){
            Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).teleport(main.spleefSpawn, PlayerTeleportEvent.TeleportCause.COMMAND);
        }else{
            sender.sendMessage(main.getPrefix(true) + "Sorry, but only a player can play spleef.");
        }
        return true;
    }
}
