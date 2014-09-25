package com.nio.pinochleserver.pinochlegames;

import naga.NIOSocket;

public interface GameStateObserver {
	public void gameOver();
	public void notifyAll(String msg);
	public void request(NIOSocket socket, String msg);
}
