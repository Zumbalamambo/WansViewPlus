package net.ajcloud.wansviewplus.main.device.type.camera;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import net.ajcloud.wansviewplus.BuildConfig;

import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.ReverseAudioInfo;

/**
 * Created
 */
public class AudioSender_RealTime {

    //it needs to be a single-channel (monaural),
    //little-endian, unheadered 16-bit signed PCM audio file.
    public  int frequency_play = 44100;
    public int frequency_send = 0;
    private static int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    private static int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int VOICE_ONCE_SIZE = 1280;
    private int GetVoice_LeftLength = 0;
    private int GetVoiceLength = 0;
    private int recBufSize = AudioRecord.ERROR_BAD_VALUE;

    public boolean AudioRecordTimerRun = false;
    public boolean PlayVlcAudioRun = false;
    public boolean isWait = true;
    public boolean sendComplete = false;
    public boolean cancelRecord = false;

    private Context mContext;
    private String cid;
    private AudioRecord audioRecord = null;

    public MediaPlayer mMediaPlayer;

    public byte[] tempResult;
    public boolean bForceOffVlc = false;
    public QueueByCustom AudioQueue = new QueueByCustom();
    public QueueByCustom EfQueue = new QueueByCustom();

    public boolean IsMute = false;
    public Thread PlayVlcAudioThread = null;
    public Thread SendAudioThread = null;
    public ReverseAudioInfo audioInfo;
    public boolean bDynamicswitchEC = true;

    private AudioManager am;
    private int initVoice = 5;



    public AudioSender_RealTime(Context context, String cid, ReverseAudioInfo audioInfo, int AudioplaySample) {
        this.mContext = context;
        this.cid = cid;
        this.audioInfo = audioInfo;
        this.frequency_play = AudioplaySample;

        /*audioInfo.iSampleRate  -2:获取失败  -1：设备侧不支持动态关闭EC*/
        if(audioInfo.getiSampleRate() != -1) {
            bDynamicswitchEC = true;
        } else {
            bDynamicswitchEC = false;
        }

        if(audioInfo.getiSampleRate() <= 0){
            audioInfo.setiSampleRate(AudioplaySample);
        }

        frequency_send =  audioInfo.getiSampleRate();
        AudioRecordTimerRun = false;
        PlayVlcAudioRun = true;
        bForceOffVlc = false;

        sendComplete = false;
        cancelRecord = false;

        am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        if (BuildConfig.DEBUG) Log.d("AudioSender_RealTime", "recBufSize:" + recBufSize);
    }

    public AudioTrack mAudioTrack = null;

    private void createAudioRecord() {
        try {
            recBufSize = AudioRecord.getMinBufferSize(frequency_send, channelConfiguration, audioEncoding);
            VOICE_ONCE_SIZE = recBufSize;
            GetVoice_LeftLength = recBufSize % 320;
            GetVoiceLength = recBufSize - GetVoice_LeftLength;

            if(GetVoiceLength >1280) {
                GetVoiceLength = 1280;
            } else if(GetVoiceLength < 640) {
                GetVoiceLength = 640;
            }

            tempResult = new byte[GetVoiceLength];
            for(int i = 0; i < GetVoiceLength; i++) {
                tempResult[i] = (byte)(0);
            }

            Log.d("recBufSize", "AudioSender_RealTime recBufSize:" + recBufSize);
            if (recBufSize < 0) {
                throw new IllegalStateException("getInstance() failed : no suitable audio configurations on this device.");
            }

            if(audioRecord != null) {
                Log.d("send_usetime", "old AudioRecord fail:" + audioRecord.getState());
            }

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, frequency_send, channelConfiguration, audioEncoding, recBufSize);

            if(audioRecord != null) {
                Log.d("send_usetime", "old AudioRecord fail:" + audioRecord.getState());
            }

            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                if (BuildConfig.DEBUG) {
                    Log.d("AudioSender_RealTime", "AudioSender sampleRate:" + frequency_send);
                }
                return;
            }
            Log.d("send_usetime", "new AudioRecord fail:" + audioRecord.getState());
            audioRecord.release();
            audioRecord = null;
            return;
        } catch (Exception e) {
            // Do nothing
        }

        throw new IllegalStateException("getInstance() failed : no suitable audio configurations on this device.");
    }

    private void createAudioPlay() {
        // Try to initialize
        try {
            // 获得构建对象的最小缓冲区大小
            int minBufSize = AudioTrack.getMinBufferSize(frequency_play,
                    AudioFormat.CHANNEL_OUT_MONO,
                    audioEncoding);
//               STREAM_ALARM：警告声
//               STREAM_MUSCI：音乐声，例如music�?
//               STREAM_RING：铃�?
//               STREAM_SYSTEM：系统声�?
//               STREAM_VOCIE_CALL：电话声�?
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    frequency_play,
                    AudioFormat.CHANNEL_OUT_MONO,
                    audioEncoding,
                    minBufSize,
                    AudioTrack.MODE_STREAM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPlayVlcAudio() {
        if (BuildConfig.DEBUG) {
            Log.d("AudioSender", "开始播放VLC音频");
        }
        PlayVlcAudioRun = true;
        if(null != PlayVlcAudioThread && PlayVlcAudioThread.isAlive()) {
            return;
        }

        AudioQueue = new QueueByCustom();
        EfQueue = new QueueByCustom();
        PlayVlcAudioThread = new Thread(PlayAudioRunnable);
        PlayVlcAudioThread.start();
    }

    public void stopPlayVlcAudio() {
        if (BuildConfig.DEBUG) {
            Log.d("AudioSender", "end播放VLC音频");
        }
        PlayVlcAudioRun = false;
    }


    public void startRecord() {
        if (BuildConfig.DEBUG) {
            Log.d("AudioSender", "开始录制语");
        }
        AudioRecordTimerRun = true;
        isWait = false;
        SendAudioThread = new Thread(SendAudioRunnable);
        SendAudioThread.start();
    }

    public  void SetMute()
    {
        IsMute = true;
    }

    public  void CancelMute()
    {
        IsMute = false;
    }


    public void stopRecord() {
        if (BuildConfig.DEBUG) Log.d("AudioSender", "结束录制语音");
        AudioRecordTimerRun = false;
    }

    public boolean isPlayAudioEnd() {
        return PlayVlcAudioThread.isAlive();
    }

    public boolean isSendAudioEnd() {
        return SendAudioThread.isAlive();
    }

    public void cancelRecord() {
        if (BuildConfig.DEBUG) {
            Log.d("AudioSender", "取消录制语音");
        }
        AudioRecordTimerRun = false;
        cancelRecord = true;
    }

    Runnable PlayAudioRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("send_usetime", "Playthread start");
            createAudioRecord();
            createAudioPlay();

            byte[] RecordBuffer = new byte[VOICE_ONCE_SIZE];
            boolean bShowTipOnce = true;

            //开始播放
            mAudioTrack.play();
            long last=System.currentTimeMillis();

            mMediaPlayer.OpRealTime(1, audioInfo.getiSampleRate());
            mMediaPlayer.SetRealTimeTalkFlag(0);

            byte[] EfData =  new byte[VOICE_ONCE_SIZE];

            long begin=System.currentTimeMillis();
            int bufferReadResult;

            while (PlayVlcAudioRun) {
                long end=System.currentTimeMillis();

                mMediaPlayer.GetEfData(VOICE_ONCE_SIZE,EfData);

                try {
                    if(!IsMute) {
                        int size = mAudioTrack.write(EfData, 0, VOICE_ONCE_SIZE);
                    }
                }catch  (Exception e) {
                    e.printStackTrace();
                    mAudioTrack.stop();
                    break ;
                }

                if (AudioRecordTimerRun) {
                        if (null != audioRecord  && audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                            audioRecord.startRecording();
                        }

                        if (audioRecord != null) {
                            bufferReadResult = audioRecord.read(RecordBuffer, 0, VOICE_ONCE_SIZE);  //480*2=960
                            if (BuildConfig.DEBUG) {
                                Log.d("AudioSender_RealTime1:", String.valueOf(bufferReadResult));
                            }
                            if (bufferReadResult <= 0) {
                                continue;
                            }
                        } else {
                            if (bShowTipOnce) {
                                //ToastUtil.makeText(R.string.videoplayer_audio_permission_error, Toast.LENGTH_SHORT).show();
                                bShowTipOnce = false;
                            }
                        }


                    synchronized (AudioSender_RealTime.this) {
                        if(AudioQueue.QueueLeft() >= VOICE_ONCE_SIZE) {
                            AudioQueue.EnQueue(RecordBuffer,VOICE_ONCE_SIZE);
                            EfQueue.EnQueue(EfData,VOICE_ONCE_SIZE);
                        }
                        if (BuildConfig.DEBUG) {
                            Log.d("send_usetime", "AudioSender_RealTime.this.notify()");
                        }

                        AudioSender_RealTime.this.notify();
                    }
                } else {
                    if (null != audioRecord  && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                        audioRecord.stop();
                    }
                }
            }

            if (audioRecord != null) {
                Log.d("send_usetime", "audioRecord release");
                audioRecord.stop();
                audioRecord.release();
            }

            mMediaPlayer.OpRealTime(0, audioInfo.getiSampleRate());

            if (mAudioTrack != null) {
                mAudioTrack.stop();
                mAudioTrack.release();
                am.setStreamVolume(AudioManager.STREAM_MUSIC, initVoice, AudioManager.FLAG_PLAY_SOUND);
                am.setMode(AudioManager.MODE_NORMAL);
            }

            AudioRecordTimerRun = false;
            synchronized (AudioSender_RealTime.this) {
                AudioSender_RealTime.this.notify();
            }
            Log.d("send_usetime", "Playthread finish");
        }
    };

    Runnable SendAudioRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("send_usetime", "SendAudioThread start");
            byte[] AudioData = null;
            byte[] EfAudioData = null;
            long old_time = 0;

            if (bDynamicswitchEC) {
                mMediaPlayer.ReverseAudioSetParameter(0, audioInfo.getiSampleRate(), audioInfo.getiChannels(), audioInfo.geteTransport());

                mMediaPlayer.ReverseAudioPlay();
            }

            while (AudioRecordTimerRun) {
                long start_time = System.currentTimeMillis();
                synchronized (AudioSender_RealTime.this) {
                    while(GetVoiceLength > AudioQueue.GetQueueLength() && AudioRecordTimerRun) {
                        try {
                            AudioSender_RealTime.this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    AudioData = AudioQueue.PopQueue(GetVoiceLength);
                    EfAudioData = EfQueue.PopQueue(GetVoiceLength);
                }

                byte[] data = new byte[GetVoiceLength*2];
                System.arraycopy(AudioData, 0, data, 0, GetVoiceLength);
                System.arraycopy(EfAudioData, 0, data, GetVoiceLength, GetVoiceLength);

                byte[] reslut =  new byte[GetVoiceLength];
                mMediaPlayer.audioEchoCancel(data, data.length,reslut);
                int length = mMediaPlayer.RealTimeTalkRtp(reslut, reslut.length, 0);
                long end_time = System.currentTimeMillis();
                long spend_time = end_time - old_time;
                old_time = end_time;
            }

            if(bDynamicswitchEC) {
                mMediaPlayer.ReverseAudioStop();
            }

            Log.d("send_usetime", "SendAudioThread finish");
        }
    };
}
