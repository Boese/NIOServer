package com.nio.pinochleserver.pinochlegames;

public interface PinochleGameSubject {
	public void registerObserver(PinochleGameObserver observer);

    public void removeObserver(PinochleGameObserver observer);

    public void notifyObservers();
}
