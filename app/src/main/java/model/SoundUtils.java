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
        mediaPlayer.setVolume(volume, volume);
    }

    private void playSound(Context context, String filename, boolean isLooping) {
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
                        int loop = 0;
                        if (isLooping) {
                            loop = -1;
                        }
                        soundPool.play(soundID, 1, 1, 0, loop, 1);
                    } else {
                        Log.e("Error with sound", "Sound load failed");
                    }
                }
            });

        } catch (IOException e) {
            Log.e("Error with sound", "failed to load sound files", e);
        }
    }

    // release sound pool when no longer in use, like when game is paused
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    public void setSoundEffectsVolume(float volume) {
        if (soundPool == null) {
            return;
        }

        soundPool.setVolume(soundID, volume, volume);
    }
}
