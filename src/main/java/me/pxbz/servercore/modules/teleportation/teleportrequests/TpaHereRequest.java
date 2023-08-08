package me.pxbz.servercore.modules.teleportation.teleportrequests;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.ServerCore;
import me.pxbz.servercore.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TpaHereRequest extends AbstractTeleportRequest {

    public TpaHereRequest(Player sender, Player receiver) {
        super(sender, receiver);
    }

    @Override
    public void accept() {
        endTeleportRequest();
        final Player senderP = Bukkit.getPlayer(sender);
        final Player receiverP = Bukkit.getPlayer(receiver);
        if (senderP == null || receiverP == null) {
            cancel(CancelReason.EXPIRED);
            return;
        }

        if (receiverP.hasPermission(bypassTeleportDelayPermission) || teleportDelay < 1) {
            teleportComplete(receiverP, senderP, getTeleportDestination(senderP));
            return;
        }

        CoreUtils.sendIfNotBlank(senderP, Messages.TPAHERE_SENDER_ACCEPT
                .replaceAll("\\{TARGET}", receiverP.getName())
                .replaceAll("\\{DELAY}", teleportDelay + ""));
        CoreUtils.sendIfNotBlank(receiverP, Messages.TPAHERE_RECEIVER_ACCEPT
                .replaceAll("\\{TARGET}", senderP.getName())
                .replaceAll("\\{DELAY}", teleportDelay + ""));

        new BukkitRunnable() {
            int counter = 0;
            Player localSender;
            Player localReceiever;
            final Location teleportLocation = getTeleportDestination(senderP);
            final Location originalSpot = receiverP.getLocation();
            @Override
            public void run() {
                localSender = Bukkit.getPlayer(sender);
                localReceiever = Bukkit.getPlayer(receiver);

                if (localReceiever == null) TpaHereRequest.this.cancel(CancelReason.EXPIRED);
                else if (!CoreUtils.sameLocation(originalSpot, localReceiever.getLocation())) TpaHereRequest.this.cancel(CancelReason.MOVEMENT);

                else {
                    // if (localReceiever != null) teleportLocation = getTeleportDestination(receiverP);
                    if (counter == teleportDelay) {
                        if (teleportLocation == null) TpaHereRequest.this.cancel(CancelReason.UNSAFE_DESTINATION);
                        else {
                            teleportLocation.setPitch(localReceiever.getLocation().getPitch());
                            teleportLocation.setYaw(localReceiever.getLocation().getYaw());
                            localReceiever.teleport(teleportLocation);
                            CoreUtils.sendIfNotBlank(localReceiever, Messages.TP_REQUEST_TELEPORTING
                                    .replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                        }
                        cancel();
                        return;
                    }
                    counter++;
                    return;
                }
                cancel();
            }
        }.runTaskTimer(ServerCore.getInstance(), 0L, 20L);
    }

    @Override
    public void deny() {
        cancel(CancelReason.DENIED);
    }

    @Override
    final protected void cancel(CancelReason reason) {

        switch (reason) {
            case MOVEMENT:
                sendIfOnline(sender, Messages.TPAHERE_CANCELLED_SENDER_MOVEMENT.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                sendIfOnline(receiver, Messages.TPAHERE_CANCELLED_RECEIVER_MOVEMENT.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(sender).getName()));
                break;
            case EXPIRED:
                sendIfOnline(sender, Messages.TPAHERE_CANCELLED_SENDER_EXPIRED.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                sendIfOnline(receiver, Messages.TPAHERE_CANCELLED_RECEIVER_EXPIRED.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(sender).getName()));
                break;
            case DENIED:
                sendIfOnline(sender, Messages.TPAHERE_CANCELLED_SENDER_TPDENIED.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                sendIfOnline(receiver, Messages.TPAHERE_CANCELLED_RECEIVER_TPDENIED.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(sender).getName()));
                break;
            case UNSAFE_DESTINATION:
                sendIfOnline(sender, Messages.TPAHERE_CANCELLED_SENDER_UNSAFE.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                sendIfOnline(receiver, Messages.TPAHERE_CANCELLED_RECEIVER_UNSAFE.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(sender).getName()));
                break;
            case TPACANCEL:
//                sendIfOnline(sender, Messages.TPAHERE_CANCELLED_SENDER_TPACANCEL.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(receiver).getName()));
                sendIfOnline(receiver, Messages.TPAHERE_CANCELLED_RECEIVER_TPACANCEL.replaceAll("\\{TARGET}", Bukkit.getOfflinePlayer(sender).getName()));
                break;
        }

        endTeleportRequest();
    }
}
