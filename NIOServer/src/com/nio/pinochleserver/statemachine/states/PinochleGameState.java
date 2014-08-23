package com.nio.pinochleserver.statemachine.states;

import java.util.List;

import com.nio.pinochleserver.Player;
import com.nio.pinochleserver.statemachine.card.Card;
import com.nio.pinochleserver.statemachine.card.Position;
import com.nio.pinochleserver.statemachine.card.Suit;

public interface PinochleGameState {
	/*
	 * PREROUND:
	 */ 		
	public List<Card> deal();
	public boolean checkForNines();
	public void bid(Player from, int bid);
	public Player whoWonBid();
	public void passCards(Player from, Player to, List<Card> cards);
	public int calculateMeld(Suit trump, List<Card> cards);
	public boolean possibleToMakeBid();
	
	/*
	 * ROUND:
	 */ 
	public boolean playCard(Player from, Card c);
	public Player winHand();
	public boolean checkForWinner();
}
