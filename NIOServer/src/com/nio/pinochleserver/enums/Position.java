package com.nio.pinochleserver.enums;

public enum Position {
	North,
	East,
	South,
	West;
	public Position getNext(int i) {
		return values()[(ordinal()+i) % values().length];
	}
}
