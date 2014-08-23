package com.nio.echoserver;

import naga.NIOSocket;
import naga.SocketObserver;

public interface MySocketObserver extends SocketObserver{

	public void connectionBroken(NIOSocket arg0, Exception arg1);

	public void connectionOpened(NIOSocket arg0);

	public void packetReceived(NIOSocket arg0, String s);

	public void packetSent(NIOSocket arg0, Object arg1);

}
