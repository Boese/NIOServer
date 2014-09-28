package com.nio.pinochleserver.pinochlegames;

import com.nio.pinochleserver.enums.Request;

public interface GameStateSubject {
	public void registerObserver(GameStateObserver observer);
    public void removeObserver(GameStateObserver observer);

    //**Broadcast new game state
    public void notifyObservers();
    //**Request move from player
    public void notifyObservers(Request request);
    //**Gameover
    public void notifyObserversGameover();
}
