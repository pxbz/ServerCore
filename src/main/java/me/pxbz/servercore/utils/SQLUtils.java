package me.pxbz.servercore.utils;

import me.pxbz.servercore.ServerCore;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLUtils {

    private static final Logger logger = ServerCore.getInstance().getLogger();
    private static Connection conn;
    private static final String dbServer = ConfigUtils.getStr("database.server");
    private static final int dbPort = ConfigUtils.getInt("database.port");
    private static final String userName = ConfigUtils.getStr("database.username");
    private static final String password = ConfigUtils.getStr("database.password");
    private static final String url = String.format("jdbc:mysql://%s:%d?user=%s&password=%s",
            dbServer, dbPort, userName, password);
    private static final String dbName = ConfigUtils.getStr("database.database-name");

    public static boolean connect() {
        try {
            conn = DriverManager.getConnection(url);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getCentralServerIP() {
        if (conn == null && !connect()) return null;

        ResultSet resultSet = query(dbName, "select `value` from servercoreconfig where `key`='centralserverip'");
        try {
            if (resultSet == null);
            else if (resultSet.next()) {
                return resultSet.getString(1);
            }
            else {
                logger.log(Level.SEVERE, "Could not retrieve central server IP from database" +
                        "- result set is empty.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Could not retrieve central server IP from database" +
                    "- SQLException with getting central server IP..");
        }
        return null;
    }

    public static ResultSet query(String db, String sql) {
        if (conn == null && !connect()) return null;

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("use " + db);
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Could not retrieve central server IP from database - SQLException with query method.");
            return null;
        }
    }
}
