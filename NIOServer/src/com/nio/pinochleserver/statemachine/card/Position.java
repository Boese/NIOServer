package com.nio.pinochleserver.statemachine.card;

public enum Position {
	North,
	East,
	South,
	West;
	public Position getNext() {
		return values()[(ordinal()+1) % values().length];
	}
}
