package com.picenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.sound.sampled.*;

public class Music {
    public static final String SYSTEM_NAME = "picenter_music";

    private static Logger logger;

    private static UserData data;

    private static Clip activeSong;

    private static boolean active;

    private static boolean stopped = true;

    public static boolean init(Logger log) {
        logger = log;

        try {
            ObjectInputStream userDataInput = new ObjectInputStream(
                    new FileInputStream(new File(Library.MUSIC_DATA_DIRECTORY + "/playlists.list")));

            data = (UserData) userDataInput.readObject();

            userDataInput.close();
        } catch (Exception e) {
            logger.log("Error while retrieving playlists.", SYSTEM_NAME, Logger.ERROR);
            logger.logError(e, null, SYSTEM_NAME);

            active = false;

            return false;
        }

        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    while(true){
                        if(!stopped){
                            activeSong.drain();
                        }
                    }
                } catch (Exception e) {
                    logger.log("Error in audio playing thread.", SYSTEM_NAME, Logger.ERROR);
                    logger.logError(e, null, SYSTEM_NAME);
                }
            }

        }).start();

        active = true;

        return true;
    }

    public static boolean isActive() {
        return active;
    }

    public static double getProgress(){
        try {
            return activeSong.getFramePosition() * 1.0 / activeSong.getFrameLength() * 1.0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isStopped(){
        return stopped;
    }

    public static boolean close() {
        try {
            stopped = true;
            
            activeSong.stop();

            activeSong.close();
        } catch (Exception e) {
            logger.log("Error while closing player", SYSTEM_NAME, Logger.ERROR);
            logger.logError(e, null, SYSTEM_NAME);
        }

        try {
            ObjectOutputStream userDataOutput = new ObjectOutputStream(
                    new FileOutputStream(new File(Library.MUSIC_DATA_DIRECTORY + "/playlists.list")));

            userDataOutput.writeObject(data);

            userDataOutput.close();

            return true;
        } catch (Exception e) {
            logger.log("Error while writing user data", SYSTEM_NAME, Logger.ERROR);
            logger.logError(e, null, SYSTEM_NAME);

            return false;
        }
    }

    public static String[] getUserPlaylists() {
        return data.getPlaylists();
    }

    public static void playSong(String song) {
        String[] artistSong = splitSong(song);

        File songDirectory = new File(Library.MUSIC_DATA_DIRECTORY + "/songs/" + artistSong[0] + "/" + artistSong[1] + "/");
        
        try{
            songDirectory.listFiles()[0].getName();
        }catch(Exception e){
            logger.log("Installing song... Info[ Artist: " + song.split(";")[0] + ", Song: " + song.split(";")[1] + " ]", SYSTEM_NAME, Logger.INFO);

            installSong(song);

            logger.log("Done", SYSTEM_NAME, Logger.INFO);
        }
        

        try {
            try {
                stopped = true;

                activeSong.stop();
            } catch (Exception e) {}

            activeSong = AudioSystem.getClip();

            activeSong.open(AudioSystem.getAudioInputStream(songDirectory.listFiles()[0]));

            activeSong.start();

            stopped = false;
        } catch (Exception e) {
            logger.log("Error while playing song", SYSTEM_NAME, Logger.ERROR);
            logger.logError(e, null, SYSTEM_NAME);
        }
    }

    public static boolean installSong(String song) {
        String[] artistSong = splitSong(song);

        try {
            File songDirectory = new File(Library.MUSIC_DATA_DIRECTORY + "/songs/" + artistSong[0] + "/" + artistSong[1]);

            songDirectory.mkdirs();

            Runtime.getRuntime()
                    .exec(new String[] {
                            "cd \"" + songDirectory.getAbsolutePath() + "\"",
                            "python3 /usr/local/bin/youtube-dl/youtube-dl -x --audio-format wav \"ytsearch1:"
                                    + song.split(";")[1] + " " + song.split(";")[0]});

            return true;
        } catch (Exception e) {
            logger.log("Error while downloading song", SYSTEM_NAME, Logger.ERROR);
            logger.logError(e, null, SYSTEM_NAME);

            return false;
        }
    }

    private static String[] splitSong(String song) {
        String[] artistSong = song.split(";");

        String songTitle = artistSong[0].replace("\s", "").toLowerCase();
        String artist = artistSong[1].replace("\s", "").toLowerCase();

        artistSong[1] = songTitle;
        artistSong[0] = artist;

        return artistSong;
    }

    private static class UserData implements Serializable {
        private ArrayList<ArrayList<String>> playlists = new ArrayList<>();

        public ArrayList<String> getPlaylist(String name) {
            for (int i = 0; i < playlists.size(); i++) {
                if (playlists.get(i).get(0).equals(name)) {
                    return playlists.get(i);
                }
            }

            return null;
        }

        public void createNewPlaylist(String name) {
            ArrayList<String> newPlaylist = new ArrayList<>();

            newPlaylist.add(name);

            playlists.add(newPlaylist);
        }

        public boolean removePlaylist(String name) {
            for (int i = 0; i < playlists.size(); i++) {
                if (playlists.get(i).get(0).equals(name)) {
                    playlists.remove(i);

                    return true;
                }
            }

            return false;
        }

        public void addToPlaylist(String song, String playlistName) {
            for (ArrayList<String> playlist : playlists) {
                if (playlist.get(0).equals(playlistName)) {
                    playlist.add(song);
                }
            }
        }

        public boolean removeFromPlaylist(String song, String playlistName) {
            for (int i = 0; i < playlists.size(); i++) {
                if (playlists.get(i).get(0).equals(playlistName)) {
                    playlists.get(i).remove(song);

                    return true;
                }
            }

            return false;
        }

        public String[] getPlaylists() {
            String[] playlistNames = new String[playlists.size()];

            for (int i = 0; i < playlistNames.length; i++) {
                playlistNames[i] = playlists.get(i).get(0);
            }

            return playlistNames;
        }
    }
}