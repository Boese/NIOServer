package com.nio.pinochleserver.pinochlegames;

import naga.NIOSocket;

public interface GameStateObserver {
	
	//Write msg to socket
	public void update(NIOSocket socket, String msg);
	
	//**Write msg to socket and start timeout response
	public void request(NIOSocket socket, String msg);
	
	//**Game is over
	public void close();
}
