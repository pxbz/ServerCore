package me.pxbz.servercore.modules.teleportation.teleportrequests;

import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.ServerCore;
import me.pxbz.servercore.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractTeleportRequest {
    private static final HashMap<UUID, LinkedHashSet<AbstractTeleportRequest>> incomingRequests = new HashMap<>();
    protected final UUID sender;
    protected final UUID receiver;
    protected final int teleportDelay = ConfigUtils.getInt("modules.teleportation.teleport-delay", 0);
    protected final String bypassTeleportDelayPermission = ConfigUtils.getStr("modules.teleportation.bypass-teleport-delay-permission");
    private BukkitTask expireTimer;

    protected enum CancelReason {
        MOVEMENT,
        EXPIRED,
        TPACANCEL,
        DENIED,
        UNSAFE_DESTINATION
    }

    public AbstractTeleportRequest(Player sender, Player receiver) {
        this.sender = sender.getUniqueId();
        this.receiver = receiver.getUniqueId();

        if (!incomingRequests.containsKey(this.receiver)) incomingRequests.put(this.receiver, new LinkedHashSet<>());
        incomingRequests.get(this.receiver).add(this);
        expireTimer = Bukkit.getScheduler().runTaskLater(ServerCore.getInstance(), () -> {
            AbstractTeleportRequest.this.cancel(CancelReason.EXPIRED);
            AbstractTeleportRequest.this.expireTimer = null;
        }, 20L*10);
    }

    public static AbstractTeleportRequest getLastIncomingRequest(Player player) {
        LinkedHashSet<AbstractTeleportRequest> hashSet = incomingRequests.get(player.getUniqueId());
        if (hashSet == null) return null;

        AbstractTeleportRequest[] tpRequestArray = new AbstractTeleportRequest[hashSet.size()];
        tpRequestArray = hashSet.toArray(tpRequestArray);
        return tpRequestArray[tpRequestArray.length - 1];
    }

    public static AbstractTeleportRequest getIncomingRequest(Player player, OfflinePlayer sender) {
        LinkedHashSet<AbstractTeleportRequest> hashSet = incomingRequests.get(player.getUniqueId());
        if (hashSet == null) return null;

        for (AbstractTeleportRequest tpRequest : hashSet) {
            if (tpRequest.sender == sender.getUniqueId()) return tpRequest;
        }
        return null;
    }

    public static void cancelAllOutgoingRequests(OfflinePlayer p) {
        for (Map.Entry<UUID, LinkedHashSet<AbstractTeleportRequest>> entrySet : incomingRequests.entrySet()) {
            entrySet.getValue().forEach(s -> s.cancel(CancelReason.TPACANCEL));
        }
        if (p.getPlayer() != null) CoreUtils.sendIfNotBlank(p.getPlayer(), Messages.TPA_CANCELLED_SENDER_TPACANCEL);
    }

    protected static void sendIfOnline(UUID uuid, String msg) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) CoreUtils.sendIfNotBlank(p, msg);
    }

    protected static Location getTeleportDestination(@NotNull Player player) {
        Location originalLoc = player.getLocation();
        Block under = originalLoc.getBlock().getRelative(BlockFace.DOWN);
        while (under.getType() == Material.AIR) {
            under = under.getRelative(BlockFace.DOWN);
            if (under.getY() < -64) return null;
        }
        Location underLoc = under.getLocation();
        underLoc.setX(underLoc.getX() + 0.5);
        underLoc.setZ(underLoc.getZ() + 0.5);
        underLoc.setY(underLoc.getY() + 1);
        return underLoc;
    }

    public abstract void accept();

    public abstract void deny();

    protected void cancel(CancelReason reason) {
        switch (reason) {
            case MOVEMENT:
                sendIfOnline(sender, Messages.TPA_CANCELLED_SENDER_MOVEMENT.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                sendIfOnline(receiver, Messages.TPA_CANCELLED_RECEIVER_MOVEMENT.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(sender).getName()));
                break;
            case EXPIRED:
                sendIfOnline(sender, Messages.TPA_CANCELLED_SENDER_EXPIRED.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                sendIfOnline(receiver, Messages.TPA_CANCELLED_RECEIVER_EXPIRED.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(sender).getName()));
                break;
            case DENIED:
                sendIfOnline(sender, Messages.TPA_CANCELLED_SENDER_TPDENIED.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                sendIfOnline(receiver, Messages.TPA_CANCELLED_RECEIVER_TPDENIED.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(sender).getName()));
                break;
            case UNSAFE_DESTINATION:
                sendIfOnline(sender, Messages.TPA_CANCELLED_SENDER_UNSAFE.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                sendIfOnline(receiver, Messages.TPA_CANCELLED_RECEIVER_UNSAFE.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(sender).getName()));
                break;
            case TPACANCEL:
//                sendIfOnline(sender, Messages.TPA_CANCELLED_SENDER_TPACANCEL.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                sendIfOnline(receiver, Messages.TPA_CANCELLED_RECEIVER_TPACANCEL.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(sender).getName()));
                break;
        }

        endTeleportRequest();
    }

    final public void endTeleportRequest() {
        if (incomingRequests.get(receiver) != null) {
            incomingRequests.get(receiver).remove(this);
            if (incomingRequests.get(receiver).isEmpty()) incomingRequests.remove(receiver);
        }
        cancelExpireTimer();
    }

    private void cancelExpireTimer() {
        if (expireTimer == null) return;
        expireTimer.cancel();
        expireTimer = null;
    }



    protected void teleportComplete(Player teleporting, OfflinePlayer teleportTo, Location teleportLocation) {

        if (teleportLocation == null) cancel(CancelReason.UNSAFE_DESTINATION);
        else {
            teleportLocation.setPitch(teleporting.getLocation().getPitch());
            teleportLocation.setYaw(teleporting.getLocation().getYaw());
            teleporting.teleport(teleportLocation);
            CoreUtils.sendIfNotBlank(teleporting, Messages.TP_REQUEST_TELEPORTING
                    .replaceAll("\\{TARGET}", teleportTo.getName()));
        }
    }
}
