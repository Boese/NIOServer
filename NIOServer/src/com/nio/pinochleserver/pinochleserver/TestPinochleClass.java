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
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.helperfunctions.CalculateMeld;


public class TestPinochleClass {

	public static void main(String[] args) throws Exception {
		List<Card> cards = new ArrayList<Card>();
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
				new Card(Suit.Hearts, Face.Ace),
				new Card(Suit.Diamonds, Face.Queen),
				new Card(Suit.Clubs, Face.King),
				new Card(Suit.Hearts, Face.Nine)
				);
		
//		JSONConvert jConvert = new JSONConvert();
//		JSONObject object = new JSONObject();
//		
//		object = jConvert.convertCardsToJSON(cards);
//		
//		System.out.println(object.toString(3));
//		
//		cards = jConvert.getCardsFromJSON(object);
//		System.out.println(cards);
		
		JSONObject one = new JSONObject();
		one.put("value", 1);
		
		JSONObject two = new JSONObject();
		two.put("value", "1");
		
		System.out.println(one.toString(2));
		System.out.println(two.toString(2));
		
//		System.out.println(cards);
//		System.out.println("Score : " + new CalculateMeld(Suit.Hearts,cards).calculate());
//		Move m = new Move();
//		m.setCards(null);
//		System.out.println(m.getCards());
	}
}
