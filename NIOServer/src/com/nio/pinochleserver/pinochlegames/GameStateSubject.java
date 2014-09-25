package com.nio.pinochleserver.pinochlegames;

public interface GameStateSubject {
	public void registerObserver(GameStateObserver observer);

    public void removeObserver(GameStateObserver observer);

    public void gameOver();
    public void notification();
    public void playerRequest();
}
