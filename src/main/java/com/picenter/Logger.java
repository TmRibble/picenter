package com.picenter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A logger that works, without using log4j. <br>
 * <br>
 * This logger keeps track of each running instance of itself and allows only
 * one instance for each log file.
 * if you attempt to create a instance of logger for a log file that already has
 * a instance running
 * it will send a warning log to that respective log file. Then return null
 * 
 * @author Tristan Ribble
 * @version {@value #VERSION}
 * @see {@link #log(String, String, int)}
 * @see {@link #logError(Exception, String, String)}
 * @see {@link #INFO}
 * @see {@link #WARNING}
 * @see {@link #ERROR}
 */
public class Logger {

    public static final String VERSION = Library.APP_VERSION;

    public static final int INFO = 0;
    public static final int WARNING = 200;
    public static final int ERROR = 400;

    private static String names = "";

    private static String old = "";

    private File logFile;
    private FileWriter writer;

    public boolean debug = false;

    /**
     * creates a new instance of the <code>Logger</code>, for the file specified.
     * 
     * @param debugMode this is only relevant when you call
     *                  {@link #logError(Exception, String, String)} if true it will
     *                  print the stack
     *                  trace to the log file, as well as error information.
     * @param fileName  the file name, a file will be created if none exists.
     * @return a logger tied to the passed in file.
     */
    public static Logger create(boolean debugMode, String fileName) {
        try {
            return new Logger(fileName, debugMode);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Logs a error to the log file. This method prints the error, with its
     * corresponding message
     * if the msg argument is null, else it prints the msg variable.
     * 
     * @param e       the exception that was thrown.
     * @param msg     a custom message, if null, print the exceptions message
     *                instead.
     * @param appName the app where the exception was thrown.
     */
    public void logError(Exception e, String msg, String appName) {
        try {
            StackTraceElement[] element = e.getStackTrace();
            String message = "";

            if (e.getMessage().equals(old)) {
                return;
            } else {
                log(e.getClass().getName() + ": "
                        + (msg == null ? (e.getMessage() == null ? "" : e.getMessage()) : msg), appName, Logger.ERROR);
            }

            if (debug) {
                message += "\n";

                for (StackTraceElement stackTraceElement : element) {
                    message += "\tat " + stackTraceElement + "\n";
                }

                message = new StringBuilder(message).replace(message.length() - 1, message.length(), "").toString();

                writer.append(message);
                writer.flush();
            }

            old = e.getMessage();

        } catch (Exception e1) {
        }
    }

    /**
     * logs a custom message to the log file. The method will not print any message
     * if the logger was closed.
     * 
     * @param message the message to print
     * @param appName the app where the log originated from
     * @param status  the status of the log. Please see {@link #INFO},
     *                {@link #WARNING}, and {@link #ERROR}.
     * @see {@link #INFO}
     * @see {@link #WARNING}
     * @see {@link #ERROR}
     */
    public void log(String message, String appName, int status) {
        try {

            writer.write(
                    (status == INFO || status == WARNING ? (status == INFO ? "INFO: " : "WARNING: ") : "ERROR: ")
                            + getCurrentTime() + ": " + appName + ": " + message + "\n");
            writer.flush();

        } catch (Exception e) {

        }
    }

    /**
     * closes the logger and frees the file up to have a new instance of logger
     * running. After calling this,
     * any successive calls of {@link #log(String, String, int)} and
     * {@link #logError(Exception, String, String)}
     * will not work
     * 
     * @see {@link #log(String, String, int)}
     * @see {@link #logError(Exception, String, String)}
     */
    public void close() {
        names = names.replace(logFile.getName().replace(".log", ""), "");

        try {
            writer.close();
        } catch (Exception e) {
        }
    }

    private static String getCurrentTime() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            return null;
        }
    }

    private Logger(String fileName, boolean debugMode) {
        try {
            this.debug = debugMode;

            logFile = new File("./log/" + fileName + ".log");

            logFile.delete();
            logFile.createNewFile();

            if(names.contains(fileName)){
                writer = new FileWriter(logFile, true);
            }else{
                writer = new FileWriter(logFile, true);
            }

            names += fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}