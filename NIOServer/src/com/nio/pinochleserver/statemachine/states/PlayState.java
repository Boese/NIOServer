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

public class PlayState implements PinochleGameState {

	private GameStateMachine gamestatemachine;
	
	public PlayState(GameStateMachine gamestatemachine) {
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
	public void bid(Player from, int bid) {
		from.setBid(bid);
		gamestatemachine.setCurrentBid(bid);
	}

	@Override
	public Player whoWonBid() {
		Player player = null;
		for (Player p : gamestatemachine.getPlayers()) {
			if(p.getBid() > player.getBid())
				player = p;
		}
		return player;
	}

	@Override
	public void passCards(Player from, Player to, List<Card> cards) {
		List<Card> tempCardsTo = to.getCurrentCards();
		for (Card card : tempCardsTo) {
			tempCardsTo.add(card);
		}
		to.setCards(cards);
	}

	@Override
	public int calculateMeld(Suit trump, List<Card> cards) {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public boolean possibleToMakeBid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean playCard(Player from, Card c) {
		System.out.println("Player : " + from.getPosition() + " Card : " + c.toString());
		return true;
	}

	@Override
	public Player winHand() {
		return gamestatemachine.getPlayers().get(0);
	}

	@Override
	public boolean checkForWinner() {
		boolean winner = false;
		if(gamestatemachine.getTeam1Score() >= 150)
			winner = true;
		if(gamestatemachine.getTeam2Score() >= 150)
			winner = true;
		return winner;
	}

}
