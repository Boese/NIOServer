package com.nio.pinochleserver.enums;

public enum GameState {
	Start,
	Deal,
	CheckForNines,
	Bid,
	SelectTrump,
	AnnounceTrump,
	PassTo,
	ReturnPass,
	Meld,
	Play,
	Pause,
	GameOver;
	public GameState getNext(int i) {
		return values()[(ordinal()+i) % values().length];
	}
}
