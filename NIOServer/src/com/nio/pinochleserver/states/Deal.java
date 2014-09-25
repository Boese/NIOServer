package com.nio.pinochleserver.states;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;
import com.nio.pinochleserver.player.Player;

public class Deal implements iPinochleState {
	Pinochle mP;
	public Deal(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Dealing...");
		mP.notification();
		deal();
		
		if(!checkForNines()) {
			mP.setState(mP.getBidState());
			mP.setBid(new Bid(mP));
		}
		else {
			mP.setCurrentMessage("Re-dealing... One Player got 5 Nines and no meld!");
			mP.notification();
		}
		
		mP.Play(null);
	}
	
	private void deal() {
		final List<Suit> suits = asList(Suit.Hearts,Suit.Diamonds,Suit.Spades,Suit.Clubs);
		final List<Face> faces = asList(Face.Nine,Face.Jack,Face.Queen,Face.King,Face.Ten,Face.Ace);
		
		List<Card> deck = new ArrayList<Card>(48);
		
		// Fill new Pinochle deck
		for (int i = 0; i < 2; i++) {	// 2 of each card *
			for (Suit suit : suits) {	// 4 of each suit *
				for (Face face : faces) {	// 6 of each face = 48 cards
					deck.add(new Card(suit,face));
				}
			}
		}
		// Shuffle deck
		Collections.shuffle(deck);
		
		// Deal out 12 cards to each player
		int from = 0;
		int to = 12;
		for (Player player : mP.getPlayers()) {
			player.setCards(deck.subList(from, to));
			from += 12;
			to += 12;
		}
	}
	
	private boolean checkForNines() {
		boolean result = false;
		for (Player p : mP.getPlayers()) {
			if(p.containsFiveNinesNoMeld())
				result = true;
		}
		return result;
	}
}
