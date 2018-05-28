package net.ajcloud.wansview.support.utils.stun;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import net.java.stun4j.StunAddress;
import net.java.stun4j.client.SimpleAddressDetector;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created
 */
public class CheckP2Pable extends AsyncTask<Void, Void, Boolean> {
    private static String endPoint1;
    private static String endPoint2;
    private Context mContext;

    public CheckP2Pable(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {

            int udpPort = 0;
            DatagramSocket session1 = null;
            int count = 0;
            while (count < 10) {
                try {
                    session1 = new DatagramSocket();
                    session1.setReuseAddress(true);
                    udpPort = session1.getLocalPort();
                    break;
                } catch (SocketException e) {
                    count++;
                    e.printStackTrace();
                }
            }
            if (count == 10)
                return false;

            String stunEndpoint1 = endPoint1;
            String[] tem = stunEndpoint1.split(":");
            SimpleAddressDetector detector = new SimpleAddressDetector(new StunAddress(tem[0], Integer.parseInt(tem[1])));
            detector.start();
            StunAddress mappedAddr = detector.getMappingFor(session1);
            session1.disconnect();
            session1.close();
            detector.shutDown();

            DatagramSocket session2 = null;
            count = 0;
            while (count < 10) {
                try {
                    session2 = new DatagramSocket(null);// 指定Null很重要，否则Java会自动随机选个可用端口来绑定
                    session2.setReuseAddress(true);
                    session2.bind(new InetSocketAddress(udpPort));
                    break;
                } catch (SocketException e) {
                    count++;
                    e.printStackTrace();
                }
            }
            if (count == 10)
                return false;

            String stunEndpoint2 = endPoint2;
            String[] tem2 = stunEndpoint2.split(":");
            SimpleAddressDetector detector2 = new SimpleAddressDetector(new StunAddress(tem2[0], Integer.parseInt(tem2[1])));
            detector2.start();
            StunAddress mappedAddr2 = detector2.getMappingFor(session2);
            session2.disconnect();
            session2.close();
            detector2.shutDown();

            try {
                Log.d("CheckP2Pable", mappedAddr.toString() + "-->VS<--" + mappedAddr2.toString());
            } catch (Exception e) {

            }
            if (mappedAddr == null || mappedAddr2 == null)
                return false;
            return mappedAddr.equals(mappedAddr2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("CheckP2Pable", "p2p is available? " + result);
        //AppApplication.P2Pable = result;
    }

    public void work() {
        if (!TextUtils.isEmpty(endPoint1) && !TextUtils.isEmpty(endPoint2)) {
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public static String getEndPoint1() {
        return endPoint1;
    }

    public static void setEndPoint1(String endPoint1) {
        CheckP2Pable.endPoint1 = endPoint1;
    }

    public static String getEndPoint2() {
        return endPoint2;
    }

    public static void setEndPoint2(String endPoint2) {
        CheckP2Pable.endPoint2 = endPoint2;
    }
}
