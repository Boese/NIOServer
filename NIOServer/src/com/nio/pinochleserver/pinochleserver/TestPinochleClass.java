package com.nio.pinochleserver.pinochleserver;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.helperfunctions.CalculateMeld;


public class TestPinochleClass {

	public static void main(String[] args) throws Exception {
		/*List<Card> cards = new ArrayList<Card>();
		cards = asList(
				new Card(Suit.Hearts, Face.Jack),
				new Card(Suit.Hearts, Face.Queen),
				new Card(Suit.Hearts, Face.King),
				new Card(Suit.Hearts, Face.Ten),
				new Card(Suit.Hearts, Face.Ace),
				new Card(Suit.Hearts, Face.Jack),
				new Card(Suit.Hearts, Face.Queen),
				new Card(Suit.Hearts, Face.King),
				new Card(Suit.Hearts, Face.Ten),
				new Card(Suit.Diamonds, Face.Queen),
				new Card(Suit.Clubs, Face.King)
				);
		
		System.out.println(cards);
		System.out.println("Score : " + new CalculateMeld(Suit.Hearts,cards).calculate());*/
		PinochleDriver d = new PinochleDriver();
		d.startGame();
	}
}
