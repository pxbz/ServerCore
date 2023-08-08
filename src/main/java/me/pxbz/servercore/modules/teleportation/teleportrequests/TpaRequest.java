package me.pxbz.servercore.modules.teleportation.teleportrequests;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.ServerCore;
import me.pxbz.servercore.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TpaRequest extends AbstractTeleportRequest {

    public TpaRequest(Player sender, Player receiver) {
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

        if (senderP.hasPermission(bypassTeleportDelayPermission) || teleportDelay < 1) {
            teleportComplete(senderP, receiverP, getTeleportDestination(receiverP));
            return;
        }

        CoreUtils.sendIfNotBlank(senderP, Messages.TPA_SENDER_ACCEPT
                .replaceAll("\\{TARGET}", receiverP.getName())
                .replaceAll("\\{DELAY}", teleportDelay + ""));
        CoreUtils.sendIfNotBlank(receiverP, Messages.TPA_RECEIVER_ACCEPT
                .replaceAll("\\{TARGET}", senderP.getName())
                .replaceAll("\\{DELAY}", teleportDelay + ""));

        new BukkitRunnable() {
            int counter = 0;
            Player localSender;
            Player localReceiever;
            final Location teleportLocation = getTeleportDestination(receiverP);
            final Location originalSpot = senderP.getLocation();
            @Override
            public void run() {
                localSender = Bukkit.getPlayer(sender);
                localReceiever = Bukkit.getPlayer(receiver);

                if (localSender == null) TpaRequest.this.cancel(CancelReason.EXPIRED);
                else if (!CoreUtils.sameLocation(originalSpot, localSender.getLocation())) TpaRequest.this.cancel(CancelReason.MOVEMENT);

                else {
                    // if (localReceiever != null) teleportLocation = getTeleportDestination(receiverP);
                    if (counter == teleportDelay) {
                        teleportComplete(localSender, Bukkit.getOfflinePlayer(receiver), teleportLocation);
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

}
