package com.nio.pinochleserver.pinochleserver;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.JSONConvert;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.helperfunctions.CalculateMeld;

public class TestPinochleClass {

	public static void main(String[] args) throws Exception {
		List<Card> cards = new ArrayList<Card>();
		cards = asList(
				new Card(Suit.Hearts, Face.Queen),
				new Card(Suit.Hearts, Face.King),
				new Card(Suit.Hearts, Face.Ten),
				new Card(Suit.Spades, Face.Nine),
				new Card(Suit.Spades, Face.Queen),
				new Card(Suit.Diamonds, Face.Nine),
				new Card(Suit.Diamonds, Face.Jack),
				new Card(Suit.Diamonds, Face.Queen),
				new Card(Suit.Diamonds, Face.King),
				new Card(Suit.Diamonds, Face.Ten),
				new Card(Suit.Clubs, Face.Jack),
				new Card(Suit.Clubs, Face.Queen)
				);
		
		CalculateMeld c = new CalculateMeld(Suit.Spades, cards);
		int x = c.calculate();
		System.out.println(x);
		//JSONConvert jConvert = new JSONConvert();
//		JSONObject object = new JSONObject();
//		
//		object = jConvert.convertCardsToJSON(cards);
//		
//		System.out.println(object.toString(3));
//		
//		cards = jConvert.getCardsFromJSON(object);
//		System.out.println(cards);
		
		
		
//		System.out.println(cards);
//		System.out.println("Score : " + new CalculateMeld(Suit.Hearts,cards).calculate());
//		Move m = new Move();
//		m.setCards(null);
//		System.out.println(m.getCards());
	}
}
