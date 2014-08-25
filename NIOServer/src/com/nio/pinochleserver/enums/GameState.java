package com.nio.pinochleserver.enums;

public enum GameState {
	Start,
	Deal,
	CheckForNines,
	Bid,
	Pass,
	Play,
	Pause,
	GameOver;
	public GameState getNext(int i) {
		return values()[(ordinal()+i) % values().length];
	}
}
