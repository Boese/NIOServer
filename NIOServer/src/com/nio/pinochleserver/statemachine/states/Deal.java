package com.nio.pinochleserver.statemachine.states;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nio.pinochleserver.statemachine.card.Card;
import com.nio.pinochleserver.statemachine.card.Face;
import com.nio.pinochleserver.statemachine.card.Suit;


public class Deal implements PinochleGameState {

	private static final List<Suit> suits = asList(Suit.Hearts,Suit.Diamonds,Suit.Spades,Suit.Hearts);
	private static final List<Face> faces = asList(Face.Nine,Face.Jack,Face.Queen,Face.King,Face.Ten,Face.Ace);
	
	private List<Card> deck = new ArrayList<Card>(48);
	
	@Override
	public void StartGame() {
		for (int i = 0; i < 2; i++) {
			for (Suit suit : suits) {
				for (Face face : faces) {
					deck.add(new Card(suit,face));
				}
			}
		}
		Collections.shuffle(deck);
		
		int i = 1;
		for (Card card : deck) {
			System.out.println(i + " " + card);
			i++;
		}
	}

	@Override
	public void StartBidding() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void PassCards() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StartRound() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void CheckForWinner() {
		// TODO Auto-generated method stub
		
	}

}
