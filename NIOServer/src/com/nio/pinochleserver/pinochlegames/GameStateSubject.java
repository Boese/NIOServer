package com.nio.pinochleserver.pinochlegames;

import com.nio.pinochleserver.enums.Request;

import naga.NIOSocket;

public interface GameStateSubject {
	public void registerObserver(GameStateObserver observer);

    public void removeObserver(GameStateObserver observer);

    public void notifyObservers();
    public void notifyObservers(Request request);
}
