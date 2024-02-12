package org.spigot.plugin.kyocuti;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.spigot.plugin.kyocuti.main.getPrefix;
import static org.spigot.plugin.kyocuti.main.worldName;

public class spleefListener implements Listener {
    private final Map<UUID, Location> playerList = new HashMap<>();
    private final ArrayList<Player> outPlayers = new ArrayList<>();
    private Boolean isGameRunning = false;
    private UUID tplDontHandle = null;


    public spleefListener(main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity().getType().toString().equals("PLAYER") && event.getCause().toString().equals("VOID") && event.getEntity().getWorld().getName().equals(worldName)) {
            event.setCancelled(true);
            if (isGameRunning) {
                tplDontHandle = event.getEntity().getUniqueId();
                event.getEntity().teleport(main.viewerPlatform, PlayerTeleportEvent.TeleportCause.PLUGIN);
                Bukkit.getPlayer(event.getEntity().getUniqueId()).setGameMode(GameMode.SPECTATOR);
                outPlayers.add((Player) event.getEntity());
                if (playerList.size()-outPlayers.size() == 1){
                    Player winner = null;
                    for (Player p : outPlayers) if (!playerList.containsKey(p.getUniqueId())) winner = p;
                    assert winner != null;
                    for (Player p : getPlayers(worldName)) {
                        if (p != winner){
                            p.sendMessage(getPrefix(false) + "§6***********GAME OVER!***********");
                            p.sendMessage(getPrefix(false) + getWinnerMsg(winner));
                            p.sendMessage(getPrefix(false) + "§6--------------------------------");
                            p.sendMessage(getPrefix(false) + "§6You are --------------" + getPlaceMsg(p) + ". Place!");
                            p.sendMessage(getPrefix(false) + "§6********************************");
                        }

                    }
                } else {
                    for (Player p : getPlayers(worldName)) {
                        p.sendMessage(main.getPrefix(false) + ((Player) event.getEntity()).getDisplayName() + " is out!");
                        p.sendMessage(main.getPrefix(false) + "There are " + (playerList.size()-outPlayers.size()) + " players remaining!");
                    }
                }
            } else {
                ((Player) event.getEntity()).sendTitle("You are out!", "Better luck next time.", -1, -1, -1);
                tplDontHandle = event.getEntity().getUniqueId();
                event.getEntity().teleport(main.spleefSpawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
    }

    @NotNull
    private List<Player> getPlayers(String worldName) throws NullPointerException{
        if (Bukkit.getWorld(worldName) == null || Objects.requireNonNull(Bukkit.getWorld(worldName)).getPlayers().isEmpty()) {
            String msg;
            String msgConsole;
            if (Bukkit.getWorld(worldName) == null) {
                msg = getPrefix(false) + "§4§lERROR! §4The world \"" + worldName + "\" cant be found anymore. Was it deleted after startup?";
                msgConsole = getPrefix(true) + "ERROR! The world \"" + worldName + "\" cant be found anymore. Was it deleted after startup?";
            } else {
                msg = getPrefix(false) + "§4§lERROR! §4The world \"" + worldName + "\" seems to be empty, but at least 1 player is in it. Is the server lagging behind?";
                msgConsole = getPrefix(true) + "ERROR! The world \"" + worldName + "\" seems to be empty, but at least 1 player is in it. Is the server lagging behind?";
            }
            for (Player p : Bukkit.getOnlinePlayers()) if (p.hasPermission("spleef.admin")) p.sendMessage("§4§l" + msg + "\nPlease look into the servers console/logs for more information.");
            throw new IllegalStateException(msgConsole);
        } else {
            return Objects.requireNonNull(Bukkit.getWorld(worldName)).getPlayers();
        }
    }

    @NotNull
    private static String getWinnerMsg(Player winner) {
        StringBuilder gameOverMsgStringBuilder = new StringBuilder("§6WINNER: ------------------------" + winner.getDisplayName());
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

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) throws InterruptedException {
        if(event.getPlayer().getUniqueId() != tplDontHandle) {
            if (event.getTo().getWorld().getName().equals(worldName)){
                if (isGameRunning) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(getPrefix(false) + "§c§lThe Spleef game is already in progress!");
                    event.getPlayer().sendMessage(getPrefix(false) + "§cPlease try again later.");
                } else {
                    int maxPlayers = 10;
                    if (getPlayers(worldName).size() < maxPlayers) {
                        playerList.put(event.getPlayer().getUniqueId(), event.getFrom());
                        for (Player p : getPlayers(worldName)) {
                            if (p.getUniqueId() != event.getPlayer().getUniqueId()) p.sendMessage(getPrefix(false) + "§8[§2+§8]§r " + event.getPlayer().getDisplayName());
                        }
                        if (getPlayers(worldName).size() == maxPlayers) {
                            for (Player p : getPlayers(worldName))p.sendTitle("30", "The game starts in:", -1, -1, -1);
                            wait(10000);
                            if (getPlayers(worldName).size() == maxPlayers) {
                                for (Player p : getPlayers(worldName))p.sendTitle("20", "The game starts in:", -1, -1, -1);
                                wait(5000);
                                if (getPlayers(worldName).size() == maxPlayers) {
                                    for (Player p : getPlayers(worldName))p.sendTitle("15", "The game starts in:", -1, -1, -1);
                                    wait(5000);
                                    if (getPlayers(worldName).size() == maxPlayers) {
                                        for (Player p : getPlayers(worldName))p.sendTitle("10", "The game starts in:", -1, -1, -1);
                                        wait(5000);
                                        if (getPlayers(worldName).size() == maxPlayers) {
                                            for (Player p : getPlayers(worldName))p.sendTitle("5", "The game starts in:", -1, -1, -1);
                                            wait(2000);
                                            if(getPlayers(worldName).size() == maxPlayers) {
                                                for (Player p : getPlayers(worldName))p.sendTitle("3", "The game starts in:", -1, -1, -1);
                                                wait(1000);
                                                if(getPlayers(worldName).size() == maxPlayers) {
                                                    for (Player p : getPlayers(worldName))p.sendTitle("2", "The game starts in:", -1, -1, -1);
                                                    wait(1000);
                                                    if(getPlayers(worldName).size() == maxPlayers) {
                                                        for (Player p : getPlayers(worldName))p.sendTitle("1", "The game starts in:", -1, -1, -1);
                                                        wait(1000);
                                                        if (getPlayers(worldName).size() == maxPlayers){
                                                            main.SpleefStarts starts = new main.SpleefStarts();
                                                            int i = 1;
                                                            for (Player p : getPlayers(worldName)) {
                                                                tplDontHandle = p.getUniqueId();
                                                                switch (i){
                                                                    case 1:{
                                                                        p.teleport(starts.ONE);
                                                                        break;
                                                                    }
                                                                    case 2: {
                                                                        p.teleport(starts.TWO);
                                                                        break;
                                                                    }
                                                                    case 3: {
                                                                        p.teleport(starts.THREE);
                                                                        break;
                                                                    }
                                                                    case 4: {
                                                                        p.teleport(starts.FOUR);
                                                                        break;
                                                                    }
                                                                    case 5: {
                                                                        p.teleport(starts.FIVE);
                                                                        break;
                                                                    }
                                                                    case 6:{
                                                                        p.teleport(starts.SIX);
                                                                        break;
                                                                    }
                                                                    case 7: {
                                                                        p.teleport(starts.SEVEN);
                                                                        break;
                                                                    }
                                                                    case 8: {
                                                                        p.teleport(starts.EIGHT);
                                                                        break;
                                                                    }
                                                                    case 9: {
                                                                        p.teleport(starts.NINE);
                                                                        break;
                                                                    }
                                                                    case 10: {
                                                                        p.teleport(starts.TEN);
                                                                        break;
                                                                    }
                                                                }
                                                                p.sendTitle("GOGOGO!!", null, -1, -1, -1);
                                                                i++;
                                                            }
                                                        }else{
                                                            for (Player p : getPlayers(worldName)) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                                        }
                                                    } else {
                                                        for (Player p : getPlayers(worldName)) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                                    }
                                                } else {
                                                    for (Player p : getPlayers(worldName)) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                                }
                                            } else {
                                                for (Player p : getPlayers(worldName)) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                            }
                                        } else {
                                            for (Player p : getPlayers(worldName)) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                        }
                                    } else {
                                        for (Player p : getPlayers(worldName)) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                    }
                                } else {
                                    for (Player p : getPlayers(worldName)) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                }
                            } else {
                                for (Player p : getPlayers(worldName)) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                            }
                        }
                    } else {
                        if (event.getPlayer().hasPermission("spleef.priority")) {
                            boolean allePrio = true;
                            for (Player p : getPlayers(worldName)) {
                                if (!(p.hasPermission("spleef.priority"))) allePrio = false;
                            }
                            if (allePrio) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage(getPrefix(false) + "§c§lSpleef is full!");
                                event.getPlayer().sendMessage(getPrefix(false) + "§cTry again later.");
                            } else {
                                int randomNum = (int) Math.floor(Math.random() * getPlayers(worldName).size());
                                Player kickedPlayer = getPlayers(worldName).get(randomNum);
                                while (kickedPlayer.hasPermission("spleef.priority")) {
                                    randomNum = (int) Math.floor(Math.random() * getPlayers(worldName).size());
                                    kickedPlayer = getPlayers(worldName).get(randomNum);
                                }
                                UUID kickedUuid = kickedPlayer.getUniqueId();
                                Location kickedLoc = playerList.get(kickedUuid);
                                playerList.remove(kickedUuid, kickedLoc);
                                kickedPlayer.teleport(kickedLoc);
                                kickedPlayer.sendMessage(getPrefix(false) + "§4You have been kicked from spleef: §cA priority player joined the full game");
                                for (Player p : getPlayers(worldName)) {
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
                    UUID uuid = event.getPlayer().getUniqueId();
                    Location location = playerList.get(uuid);
                    playerList.remove(uuid, location);
                    for (Player p : getPlayers(worldName)) {
                        if (p.getUniqueId() != uuid) p.sendMessage(getPrefix(false) + "§8[§4-§8]§r " + event.getPlayer().getDisplayName());
                    }
                }
            }
        } else {
            //Der Teleport Handler soll den teleportierten Spieler nicht handlen.
            tplDontHandle = null;
        }
    }
}
