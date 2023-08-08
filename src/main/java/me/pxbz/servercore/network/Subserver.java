package me.pxbz.servercore.network;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.utils.SQLUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Subserver extends Thread {

    private String name;
    private final int port;
    private PrintWriter writer;
    private ReadThread readThread;

    public Subserver(String name, int port) {
        this.name = name;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(SQLUtils.getCentralServerIP(), port);


            writer = new PrintWriter(socket.getOutputStream(), true);
            readThread = new ReadThread(socket, this);
            readThread.start();
            if (name != null) setServerName(this.name);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setServerName(String name) {
        this.name = name;
        writer.println("setname:" + this.name);
    }

    public void broadcast(String msg) {
        writer.println("broadcastall:" + CoreUtils.uncolour(msg));
    }

    public void sendGlobalChat(String msg) {
        writer.println("globalchat:" + msg);
    }
}
