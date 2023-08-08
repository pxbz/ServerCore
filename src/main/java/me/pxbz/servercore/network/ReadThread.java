package me.pxbz.servercore.network;

import me.pxbz.servercore.modules.chat.ChatManager;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {

    private final Socket socket;
    private final Subserver subserver;
    private boolean closeThread = false;
    private BufferedReader reader;

    public ReadThread(Socket socket, Subserver subserver) {
        this.socket = socket;
        this.subserver = subserver;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!closeThread) {
                String response = reader.readLine();
                handleResponse(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void closeConnection() {
        closeThread = true;
        try {socket.close();} catch (IOException e) {e.printStackTrace();}
    }

    void handleResponse(String msg) {
        if (msg == null) closeConnection();
        else if (msg.startsWith("broadcast:"))
            Bukkit.broadcastMessage("\n" + CoreUtils.colour(msg.substring(10)) + "\n");
        else if (msg.startsWith("globalchat:")) {
            for (Player p : ChatManager.getGlobalChatMembers()) {
                p.sendMessage(CoreUtils.colour(msg.substring(11)));
            }
        }
//        else if (msg.startsWith("blacklist:")) {
//
//        }
        else if (msg.equals("disconnect")) closeConnection();
    }
}
