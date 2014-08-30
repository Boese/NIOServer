package com.nio.pinochleserver.enums;

import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
	public int compare(Card card1, Card card2) {
		int result = card1.suit.ordinal() - card2.suit.ordinal();
		if(result == 0) {
			return card1.face.ordinal() - card2.face.ordinal();
		}
		else
			return result;
	}
}
