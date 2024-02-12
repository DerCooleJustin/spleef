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
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.spigot.plugin.spleef.main.getPrefix;
import static org.spigot.plugin.spleef.main.worldName;

public class spleefListener implements Listener {
    private final Map<UUID, Location> playerList = new HashMap<>();
    private final ArrayList<Player> outPlayers = new ArrayList<>();
    private Boolean isGameRunning = false;
    private UUID tplDontHandle = null;
    private final ArrayList<Player> playersWhoLeft = new ArrayList<>();
    private final main plugin;
    private final int maxPlayers = 10;

    public spleefListener(main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity().getType().toString().equals("PLAYER") && event.getCause().toString().equals("VOID") && event.getEntity().getWorld().getName().equals(worldName)) {
            event.setCancelled(true);
            if (isGameRunning) {
                tplDontHandle = event.getEntity().getUniqueId();
                event.getEntity().teleport(main.viewerPlatform, PlayerTeleportEvent.TeleportCause.PLUGIN);
                Objects.requireNonNull(Bukkit.getPlayer(event.getEntity().getUniqueId())).setGameMode(GameMode.SPECTATOR);
                outPlayers.add((Player) event.getEntity());
                if (playerList.size()-outPlayers.size() == 1){
                    this.isGameRunning = false;
                    Player winner = null;
                    for (Player p : outPlayers) if (!outPlayers.contains(p)) winner = p;
                    ArrayList<Player> players = new ArrayList<>();
                    for (UUID pUuid : this.playerList.keySet()) {
                        players.add(Bukkit.getPlayer(pUuid));
                    }
                    for (Player p : players) {
                        if (!(playersWhoLeft.contains(p))) {
                            if (p != winner) {
                                p.sendMessage(getPrefix(false) + "§6***********GAME OVER!***********");
                                assert winner != null;
                            } else {
                                p.sendMessage(getPrefix(false) + "§6************YOU WON!************");
                            }
                            p.sendMessage(getPrefix(false) + getWinnerMsg(winner));
                            p.sendMessage(getPrefix(false) + "§6--------------------------------");
                            p.sendMessage(getPrefix(false) + "§6You are ------------- " + getPlaceMsg(p) + ". Place!");
                            p.sendMessage(getPrefix(false) + "§6********************************");
                            this.tplDontHandle = p.getUniqueId();
                            p.teleport(playerList.get(p.getUniqueId()), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            playerList.remove(p.getUniqueId());
                            if (p != winner) outPlayers.remove(p);
                        } else {
                            outPlayers.remove(p);
                            playersWhoLeft.remove(p);
                        }
                    }
                } else {
                    for (Player p : getPlayers()) {
                        p.sendMessage(main.getPrefix(false) + ((Player) event.getEntity()).getDisplayName() + " is out!");
                        p.sendMessage(main.getPrefix(false) + "There are " + (playerList.size()-outPlayers.size()) + " players remaining!");
                    }
                }
            } else {
                tplDontHandle = event.getEntity().getUniqueId();
                event.getEntity().teleport(main.spleefSpawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
    }

    @NotNull
    private List<Player> getPlayers() throws NullPointerException{
        if (Bukkit.getWorld(main.worldName) == null || Objects.requireNonNull(Bukkit.getWorld(main.worldName)).getPlayers().isEmpty()) {
            String playerMsg;
            String msgConsole;
            if (Bukkit.getWorld(main.worldName) == null) {
                playerMsg = getPrefix(false) + "§4§lERROR! §4The world \"" + main.worldName + "\" cant be found anymore. Was it deleted after startup?";
                msgConsole = getPrefix(true) + "ERROR! The world \"" + main.worldName + "\" cant be found anymore. Was it deleted after startup?";
            } else {
                playerMsg = getPrefix(false) + "§4§lERROR! §4The world \"" + main.worldName + "\" seems to be empty, but at least 1 player is in it. Is the server lagging behind?";
                msgConsole = getPrefix(true) + "ERROR! The world \"" + main.worldName + "\" seems to be empty, but at least 1 player is in it. Is the server lagging behind?";
            }
            for (Player p : Bukkit.getOnlinePlayers()) if (p.hasPermission("spleef.admin")) p.sendMessage(playerMsg + "\nPlease look into the servers console/logs for more information.");
            throw new IllegalStateException(msgConsole);
        } else {
            return Objects.requireNonNull(Bukkit.getWorld(main.worldName)).getPlayers();
        }
    }

    @NotNull
    private static String getWinnerMsg(Player winner) {
        StringBuilder gameOverMsgStringBuilder = new StringBuilder("§6WINNER: ----------------------- " + winner.getDisplayName());
        if (gameOverMsgStringBuilder.length() > 34) {
            while (!(gameOverMsgStringBuilder.length() == 34)) {
                gameOverMsgStringBuilder.deleteCharAt(11);
            }
        } else if (gameOverMsgStringBuilder.length() < 34) {
            while (!(gameOverMsgStringBuilder.length() == 34)) {
                gameOverMsgStringBuilder.insert(11, "-");
            }
        }
        return gameOverMsgStringBuilder.toString();
    }

    @NotNull
    private String getPlaceMsg(Player player){
        if (outPlayers.contains(player)){
            int number = outPlayers.indexOf(player);
            String string;
            if (number >= 0 && number < 10) {
                string = "0" + outPlayers.indexOf(player);
            } else {
                string = String.valueOf(outPlayers.indexOf(player));
            }
            return string;
        } else {
            return "00";
        }
    }
    private main.SpleefStarts = new main.SpleefStarts() //TODO

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.getPlayer().getUniqueId() != tplDontHandle) {
            if (Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getName().equals(worldName)){
                if (isGameRunning) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(getPrefix(false) + "§c§lThe Spleef game is already in progress!");
                    event.getPlayer().sendMessage(getPrefix(false) + "§cPlease try again later.");
                } else {
                    if (getPlayers().size() < maxPlayers) {
                        playerList.put(event.getPlayer().getUniqueId(), event.getFrom());
                        for (Player p : getPlayers()) {
                            if (p.getUniqueId() != event.getPlayer().getUniqueId()) p.sendMessage(getPrefix(false) + "§8[§2+§8]§r " + event.getPlayer().getDisplayName());
                        }
                        if (getPlayers().size() == maxPlayers) {
                            final int[] countdown = {30};
                            Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
                                if (getPlayers().size() == this.maxPlayers) {
                                    if (countdown[0] > 0) {
                                        for (Player p : getPlayers()) p.sendTitle(String.valueOf(countdown[0]), "The game starts in:", -1, -1, -1);
                                        countdown[0] -= 1;
                                    } else {
                                        int i = 1;
                                        for (Player p : getPlayers()) {
                                            p.sendTitle("GO!", "Good luck!", -1, -1, -1);
                                            tplDontHandle = p.getUniqueId();
                                            switch (i) {
                                                case 1: {
                                                    p.teleport(main.SpleefStarts.) //TODO
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    for (Player p : getPlayers()) p.sendMessage(getPrefix(false) + "§c§lThe game has been cancelled.\n§cNot enough players.");
                                    Bukkit.getScheduler().cancelTasks(plugin);
                                }
                            }, 0L, 20L);
                        }
                    } else {
                        if (event.getPlayer().hasPermission("spleef.priority")) {
                            boolean allePrio = true;
                            for (Player p : getPlayers()) {
                                if (!(p.hasPermission("spleef.priority"))) allePrio = false;
                            }
                            if (allePrio) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage(getPrefix(false) + "§c§lSpleef is full!");
                                event.getPlayer().sendMessage(getPrefix(false) + "§cTry again later.");
                            } else {
                                int randomNum = (int) Math.floor(Math.random() * getPlayers().size());
                                Player kickedPlayer = getPlayers().get(randomNum);
                                while (kickedPlayer.hasPermission("spleef.priority")) {
                                    randomNum = (int) Math.floor(Math.random() * getPlayers().size());
                                    kickedPlayer = getPlayers().get(randomNum);
                                }
                                UUID kickedUuid = kickedPlayer.getUniqueId();
                                Location kickedLoc = playerList.get(kickedUuid);
                                playerList.remove(kickedUuid, kickedLoc);
                                kickedPlayer.teleport(kickedLoc);
                                kickedPlayer.sendMessage(getPrefix(false) + "§4You have been kicked from spleef: §cA priority player joined the full game");
                                for (Player p : getPlayers()) {
                                    if (p.getUniqueId() != kickedUuid) p.sendMessage(getPrefix(false) + "§8[§4-§8]§r " + kickedPlayer.getDisplayName());
                                    if (p.getUniqueId() != event.getPlayer().getUniqueId()) p.sendMessage(getPrefix(false) + "§8[§2+§8]§r " + event.getPlayer().getDisplayName());
                                }
                            }
                        } else {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(getPrefix(false) + "§c§lSpleef is full!");
                            event.getPlayer().sendMessage(getPrefix(false) + "§cTry again later.");
                        }
                    }
                }
            } else {
                if (playerList.containsKey(event.getPlayer().getUniqueId())) {
                    if (this.isGameRunning) {
                        for (Player p : getPlayers()) {
                            if (p != event.getPlayer()) {
                                p.sendMessage(getPrefix(false) + "§8[§4-§8]§r " + event.getPlayer().getDisplayName());
                                p.sendMessage(main.getPrefix(false) + "There are " + (playerList.size()-outPlayers.size()) + " players remaining!");
                            }
                        }
                        outPlayers.add(event.getPlayer());
                        playersWhoLeft.add(event.getPlayer());
                    } else {
                        for (Player p : getPlayers()) {
                            if (p != event.getPlayer()) p.sendMessage(getPrefix(false) + "§8[§4-§8]§r " + event.getPlayer().getDisplayName());
                        }
                        playerList.remove(event.getPlayer().getUniqueId(), playerList.get(event.getPlayer().getUniqueId()));
                    }
                }
            }
        } else {
            //Der Teleport Handler soll den teleportierten Spieler nicht handlen.
            tplDontHandle = null;
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Objects.requireNonNull(event.getPlayer().getLocation().getWorld()).getName().equals(worldName)) {
            if (this.isGameRunning) {
                if (this.playerList.containsKey(event.getPlayer().getUniqueId())) {
                    this.playersWhoLeft.add(event.getPlayer());
                    this.outPlayers.add(event.getPlayer());
                    for (Player p : getPlayers()) {
                        if (p != event.getPlayer()) {
                            p.sendMessage(getPrefix(false) + "§8[§4-§8]§r " + event.getPlayer().getDisplayName());
                            p.sendMessage(main.getPrefix(false) + "There are " + (playerList.size()-outPlayers.size()) + " players remaining!");
                        }
                    }
                }
            }
        }
    }
}
