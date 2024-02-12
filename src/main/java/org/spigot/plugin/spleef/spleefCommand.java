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
