package net.ajcloud.wansviewplus.main.device.type.camera;

import android.content.Context;
import android.util.Log;

import net.ajcloud.wansviewplus.support.utils.stun.StunResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;


/**
 * Created by smilence on 2014/9/10.
 */
public class PoliceHelper /*implements ResponseListener*/ {

    private static final String TAG = PoliceHelper.class.getSimpleName();
    private int mVideoHeight;
    private int mVideoWidth;
    private int mCurrentSize;
    private int playedRequestType;


    public interface PoliceControlListener {
        void onCannotPlay();

        void onPlay(int playMethod, String url, int mVideoHeight, int mVideoWidth);
    }

    private VirtualCamera virtualCamera;
    private boolean isRequestToken = false;
    private boolean isP2P = false;
    private Context context;
    private PoliceControlListener listener;

    private String urlExample;
    private String mLocation;

    //如果有个策略能够播放，则优先使用这个策略
    public static Map<String, Queue<Integer>> optimisePolice = new HashMap<String, Queue<Integer>>();
    public Queue<Integer> playPolices;

    private void initPolicies() {
        Queue<Integer> queue;
        if (optimisePolice.get(virtualCamera.cid) == null) {
            optimisePolice.put(virtualCamera.cid, new LinkedList<Integer>());
            /*if (Utils.isCameraInLan(virtualCamera.cid, virtualCamera.gwMac))
                optimisePolice.get(virtualCamera.cid).offer(PlayMethod.LAN);*/
        }
        queue = optimisePolice.get(virtualCamera.cid);
        for (int i : virtualCamera.streamPolicies)
            if (!queue.contains(i))
                queue.offer(i);
//        queue.remove(PlayMethod.LAN);
//        queue.remove(PlayMethod.UPNP);
//        queue.remove(PlayMethod.P2PX);
//        queue.remove(PlayMethod.TCP_RELAY);
//        queue.remove(PlayMethod.RTMP_RELAY);
        playPolices = queue;
    }

    public PoliceHelper(Context context, VirtualCamera virtualCamera, PoliceControlListener listener) {
        this.virtualCamera = virtualCamera;
        this.context = context;
        this.listener = listener;
        initPolicies();
    }

    public void p2pX(String relayServer, String token, StunResult[] stunResults) {
        final String url;
        try {
            isP2P = false;
            String[] wan = stunResults[0].getAddress().getAddress().toString().split("/");
            String wanip;
            if (wan.length == 2)
                wanip = wan[1];
            else
                wanip = wan[0];
            url = "rtsp://" + relayServer + new URI(urlExample).getPath() + "token=" + token
                    + "&localport0=" + stunResults[0].getMyUdpPort()
                    + "&localport1=" + stunResults[1].getMyUdpPort()
                    + "&wanport0=" + stunResults[0].getAddress().getPort()
                    + "&wanport1=" + stunResults[1].getAddress().getPort()
                    + "&wanip=" + wanip;
            Log.d(TAG, "p2px url is:" + url);
            playedRequestType = PlayMethod.P2PX;
            listener.onPlay(playedRequestType, url, mVideoHeight, mVideoWidth);
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void stunNoMatch() {
        if (!isP2P)
            return;
        isP2P = false;
        tryNextPolicy();
    }

    public void tryNextPolicy() {
        try {
            playPolices.remove();
            if (playPolices.size() > 0) {
                getUrlAndPlay();
            } else {
                initPolicies();
                listener.onCannotPlay();
            }
        } catch (NoSuchElementException e) {
            initPolicies();
            listener.onCannotPlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUrlAndPlay() {
        listener.onPlay(playedRequestType, "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov", 90, 160);
        /*if (isRequestToken) {
            return;
        }
        if (playPolices == null)
            initPolicies();
        try {
            int police;
            police = playPolices.peek();
            if ((police == PlayMethod.P2PX &&
                    (!AppApplication.P2Pable
                            || (!TextUtils.isEmpty(AppApplication.appRmtAddr)
                            && AppApplication.appRmtAddr.equals(virtualCamera.rmtAddr))))
                    || (police == PlayMethod.LAN && !Utils.isCameraInLan(virtualCamera.cid, virtualCamera.gwMac))) {
                tryNextPolicy();
                return;
            }
            Log.d("tltest", "police:"+police);
            isRequestToken = true;
            int encryptmode = -1;
            if(AppApplication.videoEncryptionInfo.isEncryptenabled()){
                encryptmode = AppApplication.videoEncryptionInfo.getEncryptmode();
            }
            HttpAdapterManger.getCameraRequest().getToken(AppApplication.devHostPresenter.getDevHost(virtualCamera.cid),  police,
                    virtualCamera.mQuality, encryptmode, new ZResponse(CameraRequest.GetStreamTokenUrl, this));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public boolean isBusy() {
        return isRequestToken || isP2P;
    }

    //@Override
    public void onSuccess(String api, Object object) {
        /*if (CameraRequest.GetStreamTokenUrl.equals(api)) {
            isRequestToken = false;
            if (object == null) {
                tryNextPolicy();
                return;
            }
            try {
                CameraToken cameraToken = (CameraToken)object;
                int reqtype = cameraToken.getReqtype();
                final String token = cameraToken.getToken();
                CameraTokenStream streamUrl = cameraToken.getStreamUrl();
                String protocol = "rtsp://";

                if(AppApplication.videoEncryptionInfo.getEncryptmode() == 1){
                    protocol = "rtsp://";
                }
                if (streamUrl == null) {
                    listener.onCannotPlay();
                    return;
                }
                mVideoHeight = streamUrl.getResheight();
                mVideoWidth = streamUrl.getReswidth();

                *//*设置视频加密相关信息*//*
                AppApplication.videoEncryptionInfo.setSessionkey(cameraToken.getSessionkey());
                AppApplication.videoEncryptionInfo.setMac(virtualCamera.ethMac);
                AppApplication.videoEncryptionInfo.setOid(virtualCamera.cid);

                String playType = "";
                switch (reqtype) {
                    case PlayMethod.LAN:
                        playedRequestType = PlayMethod.LAN;
                        urlExample = streamUrl.getLocalurl();

                        Log.d(TAG, "play by upnp");
                        playType =  "lan";
                        mLocation = urlExample + "token=" + token;
                        listener.onPlay(playedRequestType, mLocation, mVideoHeight, mVideoWidth);

                        break;
                    case PlayMethod.UPNP: {
                        playedRequestType = PlayMethod.UPNP;
                        playType = "upnp";
                        isP2P = false;
                        mLocation = streamUrl.getWanurl();
                        if (!(mLocation.equals("") || mLocation.equals("null"))) {
                            mLocation += "token=" + token;
                            listener.onPlay(playedRequestType, mLocation, mVideoHeight, mVideoWidth);
                        } else {
                            tryNextPolicy();
                        }
                        break;
                    }
                    case PlayMethod.TCP_RELAY: {
                        playType = "中继";
                        isP2P = false;
                        urlExample = streamUrl.getLocalurl();
                        playedRequestType = PlayMethod.TCP_RELAY;
                        mLocation = protocol + cameraToken.getReqserver() + new URI(urlExample).getPath() + "token=" + token;
                        listener.onPlay(playedRequestType, mLocation, mVideoHeight, mVideoWidth);
                        break;
                    }
                    case PlayMethod.P2PX: {
                        playType = "p2px";
                        isP2P = true;
                        urlExample = streamUrl.getLocalurl();
                        String stunServer = cameraToken.getStunserver();
                        String relayServer = cameraToken.getRelayserver();
                        StunManager stunManager = new StunManager(relayServer, token, this);
                        stunManager.execute(stunServer);
                        break;
                    }
                    case PlayMethod.RTMP_RELAY: {
                        playType = "RTMP中继";
                        isP2P = false;
                        String relayServer = cameraToken.getRelayserver();
                        String rtmp_token = cameraToken.getToken();
                        playedRequestType = PlayMethod.RTMP_RELAY;
                        mLocation = relayServer + rtmp_token;
                        listener.onPlay(playedRequestType, mLocation, mVideoHeight, mVideoWidth);
                        break;
                    }
                }
                CameraHomeEventReporter.setEVENT_CHPlayMode(virtualCamera.cid, playType);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

   // @Override
    public void onError(String api, int errorCode) {
        /*if (CameraRequest.GetStreamTokenUrl.equals(api)) {
            isRequestToken = false;
            tryNextPolicy();
        }*/
    }

}
