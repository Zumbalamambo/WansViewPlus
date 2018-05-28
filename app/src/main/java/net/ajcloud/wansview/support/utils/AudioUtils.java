package net.ajcloud.wansview.support.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by smilence on 2014/6/11.
 */
public class AudioUtils {

    public static AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static void setStreamMute(Context context, boolean state) {
        AudioManager audioManager = getAudioManager(context);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, state);
    }

    public static void setStreamVolume(Context context, int direction, int volume) {
        AudioManager audioManager = getAudioManager(context);
//        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, 0);
//        if (volume != 0)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    public static int getStreamVolume(Context context) {
        AudioManager audioManager = getAudioManager(context);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

}
