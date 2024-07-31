package com.picenter;

import java.io.*;
import java.net.*;

public class Backend {
    public static final String SYSTEM_NAME = "picenter_backend";

    private static boolean active = false;

    private static Logger logger;

    private static Socket connection;

    private static BufferedReader reader;
    private static BufferedWriter writer;

    public static boolean init(Logger log) {
        logger = log;

        logger.log("Testing connection to python backend...", SYSTEM_NAME, Logger.INFO);

        try {
            connection = new Socket(InetAddress.getByName(Library.NETWORK_BACKEND_ADDRESS), Library.NETWORK_BACKEND_PORT);

            logger.log("Connection established with python backend", SYSTEM_NAME, Logger.INFO);

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

            active = true;

            return true;
        } catch (Exception e) {
            if (e.getMessage().contains("refused")) {
                logger.log("Connection to python backend failed", SYSTEM_NAME, Logger.ERROR);

                active = false;

                return false;
            } else {
                logger.log("Error while connecting to python backend", SYSTEM_NAME, Logger.ERROR);
                logger.logError(e, null, SYSTEM_NAME);

                active = false;

                return false;
            }
        }
    }

    public static boolean isActive() {
        return active;
    }
}
