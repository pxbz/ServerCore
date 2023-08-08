package me.pxbz.servercore;

import me.pxbz.servercore.modules.chat.*;
import me.pxbz.servercore.modules.combat.NewbieCommand;
import me.pxbz.servercore.modules.essentials.*;
import me.pxbz.servercore.modules.misc.ServerCoreCommand;
import me.pxbz.servercore.modules.moderation.*;
import me.pxbz.servercore.modules.teleportation.*;
import me.pxbz.servercore.modules.warping.*;

public final class CommandManager {

    private CommandManager() {}

    public static void registerAllCommands() {
        new ChatColourCommand();
        new GlobalChatCommand();
        new ClearChatCommand();
        new MuteChatCommand();
        new MuteSpeakerCommand();
        new StaffChatCommand();
        new PrivateChatCommand();
        new BroadcastCommand();
        new BroadcastGlobalCommand();

        new FlyCommand();
        new GamemodeCommand();
        new VanishCommand();
        new ClearCommand();
        new UnclearCommand();
        new SudoCommand();
        new NickCommand();
        new RealNameCommand();
        new SeenCommand();

        new KickCommand();
        new KickSilentCommand();
        new BanCommand();
        new BanSilentCommand();
        new UnbanCommand();
        new UnbanSilentCommand();
        new TempBanCommand();
        new TempBanSilentCommand();

        new BackCommand();
        new TpCommand();
        new TpHereCommand();
        new TpAllCommand();
        new TpaCommand();
        new TpaHereCommand();
        new TpaAllCommand();
        new TpAcceptCommand();
        new TpaDenyCommand();
        new TpaCancelCommand();
        new TpaToggleCommand();
        new RtpCommand();

        new SpawnCommand();
        new SetSpawnCommand();
        new WarpCommand();
        new WarpsCommand();
        new SetWarpCommand();
        new DelWarpCommand();

        new ServerCoreCommand();

        new NewbieCommand();
    }
}
