package model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

public class SoundUtils {
    private SoundPool soundPool;
    private int soundID;
    private MediaPlayer mediaPlayer;
    private int musicVolume = 85;
    private int soundVolume = 80;

    // https://gamecodeschool.com/android/playing-sound-fx-demo/
    //https://www.geeksforgeeks.org/soundpool-in-android-with-examples/
    public void playMusic(Context context, String filename, boolean isLooping) {
        // release any MediaPlayer, if one exists
        releaseMediaPlayer();

        try {
            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor descriptor = context.getAssets().openFd(filename);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(),
                    descriptor.getStartOffset(),
                    descriptor.getLength());
            descriptor.close();

            mediaPlayer.setLooping(isLooping);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            Log.e("Error with sound", "Failed to load sound file", e);
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void pauseMusic() {
        mediaPlayer.pause();
    }

    public void resumeMusic() {
        mediaPlayer.start();
    }

    public void stopMusic() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.stop();
    }

    /**
     * @param volume value from 0.0 to 1.0 to control volume of music
     */
    public void setMusicVolume(float volume) {
        if (mediaPlayer == null) {
            return;
        }
        musicVolume = (int) (volume * 100);
        mediaPlayer.setVolume(volume, volume);
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    private void playSound(Context context, String filename) {
        releaseSoundPool();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // load sound in memory ready for use
            descriptor = assetManager.openFd(filename);
            soundID = soundPool.load(descriptor, 0);

            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (status == 0) {
                        // sound loaded successfully
                        float volume = soundVolume/100f;
                        soundPool.play(soundID, volume, volume, 0, 0, 1);
                    } else {
                        Log.e("Error with sound", "Sound load failed");
                    }
                }
            });

        } catch (IOException e) {
            Log.e("Error with sound", "failed to load sound files", e);
        }
    }

    public void playSelectSound(Context context) {
        playSound(context, "select.wav");
    }

    public void playConfirmSound(Context context) {
        playSound(context, "confirm.wav");
    }

    public void playCancelSound(Context context) {
        playSound(context, "cancel.wav");
    }

    public void playGetHitSound(Context context) {
        playSound(context, "take_damage.wav");
    }

    public void playHitEnemySound(Context context) {
        playSound(context, "hit_enemy.wav");
    }


    // release sound pool when no longer in use, like when game is paused
    public void releaseSoundPool() {
        if (soundPool == null) {
            return;
        }
        soundPool.release();
        soundPool = null;
    }

    public void setSoundEffectsVolume(float volume) {
        if (soundPool == null) {
            return;
        }
        soundVolume = (int) (volume * 100);
        soundPool.setVolume(soundID, volume, volume);
    }

    public int getSoundVolume() {
        return soundVolume;
    }
}
