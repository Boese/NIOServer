package com.nio.pinochleserver.enums;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nio.pinochleserver.player.Player;

public class JSONConvert {
	
	/*
	 * OPTIONS IN JSONConvert
	 * 
	 * List<Card> cards
	 * Card card
	 * integer bid
	 * Suit trump
	 * 
	 */
	
	public JSONConvert() {}
	
	/*
	 * CONVERT JSONOBJECT TO OPTION
	 */
	
	public List<Card> getCardsFromJSON(JSONObject object) {
		List<Card> returnCards = null;
		try{
			returnCards = new ArrayList<Card>();
			JSONArray jArray = object.optJSONArray("cards");
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject card = jArray.getJSONObject(i);
				returnCards.add(getCardFromJSON(card));
			}
		}
		catch(Exception e) {}
		return returnCards;
	}
	
	public Card getCardFromJSON(JSONObject object) {
		Card returnCard = null;
		try{
			Suit suit = Suit.valueOf(object.optString("suit", null));
			Face face = Face.valueOf(object.optString("face",null));
			returnCard = new Card(suit,face);
		}
		catch(Exception e) {}
		return returnCard;
	}
	
	public int getBidFromJSON(JSONObject object) {
		int bid = -1;
		try {
			bid = object.optInt("bid", -1);
		}catch(Exception e) {}
		return bid;
	}
	
	public Suit getTrumpFromJSON(JSONObject object) {
		Suit suit = null;
		try {
			suit = Suit.valueOf(object.optString("suit", null));
		}catch(Exception e) {}
		return suit;
	}
	
	/*
	 * CONVERT OPTION TO JSONOBJECT
	 */
	
	public JSONObject convertCardsToJSON(List<Card> cards) {
		JSONObject cardsJSON = null;
		try {
			cardsJSON = new JSONObject();
	        JSONArray cardArray = new JSONArray();
	        for (Card card : cards) {
	            cardArray.put(convertCardToJSON(card));
	            }
	        cardsJSON.put("cards", cardArray);
		} catch (Exception e) {}
		return cardsJSON;
	}
	
	public JSONObject convertCardToJSON(Card card) {
		JSONObject cardJSON = null;
		try{
			cardJSON = new JSONObject();
			cardJSON.put("suit", card.suit);
			cardJSON.put("face", card.face);
		}
		catch(Exception e) {}
		return cardJSON;
	}
	
	public JSONObject convertBidToJSON(int bid) {
		JSONObject intJSON = null;
		try {
			intJSON = new JSONObject();
			intJSON.put("bid", bid);
		}
		catch(Exception e) {}
		return intJSON;
	}
	
	public JSONObject convertTrumpToJSON(Suit suit) {
		JSONObject trumpJSON = null;
		try {
			trumpJSON = new JSONObject();
			trumpJSON.put("suit", suit);
		}
		catch(Exception e) {}
		return trumpJSON;
	}
}
