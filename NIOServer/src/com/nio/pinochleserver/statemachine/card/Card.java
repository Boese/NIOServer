package com.nio.pinochleserver.statemachine.card;

public class Card {
	public Suit suit;
	public Face face;
	
	public Card(Suit suit, Face face) {
		this.suit=suit;
		this.face=face;
	}
	
	@Override
	public String toString() {
		return ("Card : " + face + "," + suit);
	}
}
