package com.nio.pinochleserver.enums;

public class Card {
	public Suit suit;
	public Face face;
	
	public Card(Suit suit, Face face) {
		this.suit=suit;
		this.face=face;
	}
	
	@Override
	public String toString() {
		return (face + "," + suit);
	}
	
	@Override
	public boolean equals (Object o) {
		if(o == null)
			return false;
	    Card x = (Card) o;
	        if (x.face == face && x.suit == suit) return true;
	        return false;
	    }

	public boolean contains(Card c) {
		if(c.equals(this)) 
			return true;
		return false;
	}
	
}
