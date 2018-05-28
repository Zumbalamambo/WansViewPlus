package org.httprelay;

public class HttpRelay{
	public native int Start(String ip, String port);
	public native int GetStatus(int[] info);
	public native int Stop();

	static{
		System.loadLibrary("tcprelay");
	}
}