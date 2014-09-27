package com.nio.pinochleserver.pinochlegames;

import naga.NIOSocket;

public interface GameStateSubject {
	public void registerObserver(GameStateObserver observer);

    public void removeObserver(GameStateObserver observer);

    public void notifyObservers();
}