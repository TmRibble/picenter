package com.picenter;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioTest {
    static Clip clip;

    public static void main(String[] args) {
        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        clip = AudioSystem.getClip();

                        AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("./myhero.wav"));

                        // FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
                        // volume.setValue(1.0f);

                        clip.open(inputStream);

                        clip.start();

                        Thread.sleep(15);

                        clip.drain();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).start();

            while(true){
                try {
                    System.out.println((clip.getFramePosition() * 1.0 / clip.getFrameLength() * 1.0));
                } catch (Exception e) {
                    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
