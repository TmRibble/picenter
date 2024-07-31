package com.picenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;

public class PiCenter {
    private static Logger logger;

    public PiCenter(){
        logger = Logger.create(true, "picenter");

        logger.log("Logger created successfully", Library.APP_PRIVATE_NAME, Logger.INFO);
        logger.log("Currently running " + Library.APP_PUBLIC_NAME + " v" + Library.APP_VERSION, Library.APP_PRIVATE_NAME, Logger.INFO);

        logger.log("Initializing...", Library.APP_PRIVATE_NAME, Logger.INFO);

        logger.log("Reading config file...", Library.APP_PRIVATE_NAME, Logger.INFO);

        readConfig();

        logger.log("Initializing systems...", Library.APP_PRIVATE_NAME, Logger.INFO);

        init();
    }

    private void init() {
        boolean initSucceeded;

        initSucceeded = Backend.init(logger);
        
        logger.log("Initialization of backend " + (initSucceeded ? "succeeded" : "failed"), Library.APP_PRIVATE_NAME, initSucceeded ? Logger.INFO : Logger.ERROR);

        // initSucceeded = GoogleCalender.init(logger);
        initSucceeded = false;

        logger.log("Initialization of calender " + (initSucceeded ? "succeeded" : "failed"), Library.APP_PRIVATE_NAME, initSucceeded ? Logger.INFO : Logger.ERROR);
        
        initSucceeded = Music.init(logger);

        logger.log("Initialization of music " + (initSucceeded ? "succeeded" : "failed"), Library.APP_PRIVATE_NAME, initSucceeded ? Logger.INFO : Logger.ERROR);

        initSucceeded = Window.init(logger);

        logger.log("Initialization of graphics " + (initSucceeded ? "succeeded" : "failed"), Library.APP_PRIVATE_NAME, initSucceeded ? Logger.INFO : Logger.ERROR);
    
        Music.playSong("my hero;foo fighters");
    }

    private void readConfig() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("./picenter.conf")));

            String[] keyAndToken;
            String line;

            while((line = reader.readLine()) != null){
                keyAndToken = line.split("=");

                //if the line is not blank
                if(keyAndToken.length != 1 && !line.startsWith(";")){
                    keyAndToken[0].strip();
                    keyAndToken[1].strip();

                    if(keyAndToken[0].equals("backend-port")){
                        try{
                            Library.NETWORK_BACKEND_PORT = Integer.parseInt(keyAndToken[1]);
                        }catch (NumberFormatException e){
                            logger.log("Invalid value for option \'backend-port\'. Please ensure that value is a integer value, and try again. (Defaulting to 1331)", Library.APP_PRIVATE_NAME, Logger.WARNING);
                        }catch (Exception e){
                            logger.log("Error while parsing value for option \'backend-port\'", Library.APP_PRIVATE_NAME, Logger.ERROR);
                            logger.logError(e, null, Library.APP_PRIVATE_NAME);
                        }
                    }else if(keyAndToken[0].equals("backend-address")){
                        try{
                            if(InetAddress.getByName(keyAndToken[1]) == null){
                                logger.log("Check failed for value for option \'backend-address\'. Host does not exist. (Defaulting to \'localhost\')", Library.APP_PRIVATE_NAME, Logger.WARNING);

                                continue;
                            }else{
                                Library.NETWORK_BACKEND_ADDRESS = keyAndToken[1];
                            }
                        }catch(Exception e){
                            logger.log("Error while checking value for option \'backend-address\'", Library.APP_PRIVATE_NAME, Logger.ERROR);
                            logger.logError(e, null, Library.APP_PRIVATE_NAME);
                        }
                    }else if(keyAndToken[0].equals("frontend-port")){
                        try{
                            Library.NETWORK_FRONTEND_PORT = Integer.parseInt(keyAndToken[1]);
                        }catch (NumberFormatException e){
                            logger.log("Invalid value for option \'frontend-port\'. Please ensure that value is a integer value, and try again. (Defaulting to 3113)", Library.APP_PRIVATE_NAME, Logger.WARNING);
                        }catch (Exception e){
                            logger.log("Error while parsing value for option \'frontend-port\'", Library.APP_PRIVATE_NAME, Logger.ERROR);
                            logger.logError(e, null, Library.APP_PRIVATE_NAME);
                        }
                    }else if(keyAndToken[0].equals("music-data-directory")){
                        if(new File(keyAndToken[1]).exists()){
                            Library.MUSIC_DATA_DIRECTORY = keyAndToken[1];
                        }else{
                            logger.log("Invalid value for option \'music-data-directory\'. Please ensure that directory exists, and try again. (Defaulting to \'./music\'')", Library.APP_PRIVATE_NAME, Logger.WARNING);
                        }
                    }
                }
            }

            reader.close();
        } catch (Exception e) {
            logger.logError(e, null, Library.APP_PRIVATE_NAME);
        }
    }

    public static void main(String[] args) {
        new PiCenter();
    }
}
