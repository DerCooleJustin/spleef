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

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) throws InterruptedException {
        if(event.getPlayer().getUniqueId() != tplDontHandle) {
            if (Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getName().equals(worldName)){
                if (isGameRunning) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(getPrefix(false) + "§c§lThe Spleef game is already in progress!");
                    event.getPlayer().sendMessage(getPrefix(false) + "§cPlease try again later.");
                } else {
                    int maxPlayers = 10;
                    if (getPlayers().size() < maxPlayers) {
                        playerList.put(event.getPlayer().getUniqueId(), event.getFrom());
                        for (Player p : getPlayers()) {
                            if (p.getUniqueId() != event.getPlayer().getUniqueId()) p.sendMessage(getPrefix(false) + "§8[§2+§8]§r " + event.getPlayer().getDisplayName());
                        }
                        if (getPlayers().size() == maxPlayers) {
                            for (Player p : getPlayers())p.sendTitle("30", "The game starts in:", -1, -1, -1);
                            wait(10000);
                            if (getPlayers().size() == maxPlayers) {
                                for (Player p : getPlayers())p.sendTitle("20", "The game starts in:", -1, -1, -1);
                                wait(5000);
                                if (getPlayers().size() == maxPlayers) {
                                    for (Player p : getPlayers())p.sendTitle("15", "The game starts in:", -1, -1, -1);
                                    wait(5000);
                                    if (getPlayers().size() == maxPlayers) {
                                        for (Player p : getPlayers())p.sendTitle("10", "The game starts in:", -1, -1, -1);
                                        wait(5000);
                                        if (getPlayers().size() == maxPlayers) {
                                            for (Player p : getPlayers())p.sendTitle("5", "The game starts in:", -1, -1, -1);
                                            wait(2000);
                                            if(getPlayers().size() == maxPlayers) {
                                                for (Player p : getPlayers())p.sendTitle("3", "The game starts in:", -1, -1, -1);
                                                wait(1000);
                                                if(getPlayers().size() == maxPlayers) {
                                                    for (Player p : getPlayers())p.sendTitle("2", "The game starts in:", -1, -1, -1);
                                                    wait(1000);
                                                    if(getPlayers().size() == maxPlayers) {
                                                        for (Player p : getPlayers())p.sendTitle("1", "The game starts in:", -1, -1, -1);
                                                        wait(1000);
                                                        if (getPlayers().size() == maxPlayers){
                                                            this.isGameRunning = true;
                                                            main.SpleefStarts starts = new main.SpleefStarts();
                                                            int i = 1;
                                                            for (Player p : getPlayers()) {
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
                                                            for (Player p : getPlayers()) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                                        }
                                                    } else {
                                                        for (Player p : getPlayers()) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                                    }
                                                } else {
                                                    for (Player p : getPlayers()) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                                }
                                            } else {
                                                for (Player p : getPlayers()) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                            }
                                        } else {
                                            for (Player p : getPlayers()) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                        }
                                    } else {
                                        for (Player p : getPlayers()) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                    }
                                } else {
                                    for (Player p : getPlayers()) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                                }
                            } else {
                                for (Player p : getPlayers()) p.sendMessage(getPrefix(false) + "The game has been cancelled: Not enough players.");
                            }
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
