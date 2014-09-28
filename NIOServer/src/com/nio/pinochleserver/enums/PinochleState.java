package com.nio.pinochleserver.enums;

public enum PinochleState {
	Pause,
	Start,
	Deal,
	Bid,
	Trump,
	Pass,
	Meld,
	Round,
	Gameover;
	
	public PinochleState getNext(int i) {
		return values()[(ordinal()+i) % values().length];
	}
}
