package cn.hanabi.utils;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

public class SoundFxPlayer {
    public void playSound(SoundType st,float volume) {
        new Thread(() -> {
            AudioInputStream as;
            try {
                as = AudioSystem.getAudioInputStream(new BufferedInputStream(Objects.requireNonNull(this.getClass()
                        .getResourceAsStream("/assets/minecraft/Client/SoundFx/" + st.getName()))));
                Clip clip = AudioSystem.getClip();
                clip.open(as);
                clip.start();
                FloatControl gainControl =
                        (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume); // Reduce volume by 10 decibels.
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public enum SoundType{
        Enable("enable.wav"),
        Disable("disable.wav"),
        Enable2("enable2.wav"),
        Disable2("disable2.wav"),
        Enter("enter.wav"),
        Notification("notification.wav"),
        Startup("startup.wav"),
        ClickGuiOpen("clickguiopen.wav"),
        Ding("dingsound.wav"),
        Crack("cracksound.wav"),
        EDITION("ingame.wav"),
        VICTORY("victory.wav"),
        SPECIAL("spec.wav");

        final String name;
        SoundType(String fileName){
            this.name = fileName;
        }
        String getName(){
            return name;
        }
    }
}
