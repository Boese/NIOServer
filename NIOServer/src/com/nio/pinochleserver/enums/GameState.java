package com.nio.pinochleserver.enums;

public enum GameState {
	Start,
	Deal,
	Bid,
	Pass,
	Play,
	GameOver;
	public GameState getNext(int i) {
		return values()[(ordinal()+i) % values().length];
	}
}
