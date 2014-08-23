package com.nio.pinochleserver.statemachine.states;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nio.pinochleserver.Player;
import com.nio.pinochleserver.statemachine.GameStateMachine;
import com.nio.pinochleserver.statemachine.card.Card;
import com.nio.pinochleserver.statemachine.card.Face;
import com.nio.pinochleserver.statemachine.card.Suit;

public class Play implements PinochleGameState {

	private GameStateMachine gamestatemachine;
	
	Play(GameStateMachine gamestatemachine) {
		this.gamestatemachine=gamestatemachine;
	}
	@Override
	public List<Card> deal() {
		final List<Suit> suits = asList(Suit.Hearts,Suit.Diamonds,Suit.Spades,Suit.Hearts);
		final List<Face> faces = asList(Face.Nine,Face.Jack,Face.Queen,Face.King,Face.Ten,Face.Ace);
		
		List<Card> deck = new ArrayList<Card>(48);
		
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
		return deck;
	}

	@Override
	public boolean checkForNines() {
		boolean result = false;
		for (Player p : gamestatemachine.getPlayers()) {
			if(p.containsFiveNines())
				result = true;
		}
		return result;
	}

	@Override
	public void bid(Player player, int bid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Player whoWonBid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void passCards(Player from, Player to, List<Card> cards) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int calculateMeld(Suit trump, List<Card> cards) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean possibleToMakeBid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playCard(Player from, Card c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Player winHand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkForWinner() {
		// TODO Auto-generated method stub
		return false;
	}

}
