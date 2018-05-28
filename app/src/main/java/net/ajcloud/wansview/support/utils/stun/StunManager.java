package net.ajcloud.wansview.support.utils.stun;

import android.os.AsyncTask;
import android.util.Log;

import net.ajcloud.wansview.BuildConfig;
import net.ajcloud.wansview.main.device.type.camera.PoliceHelper;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.client.SimpleAddressDetector;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by smilence on 13-11-11.
 */
public class StunManager extends AsyncTask<String, Integer, StunResult[]> {

    private final static String TAG = StunManager.class.getSimpleName();
    String relayServer;
    String token;
    PoliceHelper policeHelper;

    public StunManager(String relayServer, String token, PoliceHelper policeHelper) {
        this.relayServer = relayServer;
        this.token = token;
        this.policeHelper = policeHelper;
    }

    int[] myUDPPort = new int[2];
    StunResult[] stunResult = new StunResult[2];

    protected StunResult[] doInBackground(String... params) {
        try {
            if (BuildConfig.DEBUG) Log.d(TAG, "doing stun");
            String stunEndpoint = params[0];
            String[] tem = stunEndpoint.split(":");
            SimpleAddressDetector detector = new SimpleAddressDetector(
                    new StunAddress(tem[0], Integer.parseInt(tem[1])));
            detector.start();
            DatagramSocket session1 = null;
            int count = 0;
            while (count < 10) {
                try {
                    session1 = new DatagramSocket();
                    session1.setReuseAddress(true);
                    myUDPPort[0] = session1.getLocalPort();
                    break;
                } catch (SocketException e) {
                    count++;
                    e.printStackTrace();
//                    ExceptionHandler.handleError(AppApplication.getAppContext(), e);
                }
            }
            StunAddress mappedAddr = detector.getMappingFor(session1);
//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "final address1 is " + mappedAddr.getSocketAddress().getAddress() + ":" + mappedAddr.getSocketAddress().getPort());
            if (mappedAddr.getSocketAddress() == null)
                return null;
            stunResult[0] = new StunResult(myUDPPort[0], mappedAddr.getSocketAddress());

            DatagramSocket session2 = null;
            StunAddress mappedAddr2;
            do {
                count = 0;
                while (count < 10) {
                    try {
                        session2 = new DatagramSocket();
                        session2.setReuseAddress(true);
                        myUDPPort[1] = session2.getLocalPort();
                        if (Math.abs(myUDPPort[1] - myUDPPort[0]) < 2) {
                            count++;
                            continue;
                        }
                        break;
                    } catch (SocketException e) {
                        count++;
                        e.printStackTrace();
//                        ExceptionHandler.handleError(AppApplication.getAppContext(), e);
                    }
                }
                mappedAddr2 = detector.getMappingFor(session2);
                try {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "address is " + mappedAddr2.getSocketAddress().getAddress() + ":" + mappedAddr2.getSocketAddress().getPort());
                } catch (Exception e) {

                }
            }
            while (Math.abs(mappedAddr2.getSocketAddress().getPort() - mappedAddr.getSocketAddress().getPort()) < 2);

            if (BuildConfig.DEBUG)
                Log.d(TAG, "final address is " + mappedAddr2.getSocketAddress().getAddress() + ":" + mappedAddr2.getSocketAddress().getPort());
            stunResult[1] = new StunResult(myUDPPort[1], mappedAddr2.getSocketAddress());
            detector.shutDown();
            return stunResult;
        } catch (StunException e) {
//            ExceptionHandler.handleError(AppApplication.getAppContext(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(StunResult[] stunResults) {
        if (stunResults != null)
            policeHelper.p2pX(relayServer, token, stunResults);
        else
            policeHelper.stunNoMatch();
    }


}
