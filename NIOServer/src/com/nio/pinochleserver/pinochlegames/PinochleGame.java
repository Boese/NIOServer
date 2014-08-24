package com.nio.pinochleserver.pinochlegames;

import java.util.List;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.player.Player;

public interface PinochleGame {
	/*
	 * PREROUND:
	 */ 		
	public void deal();
	public boolean checkForNines();
	public Position startBid();
	public boolean bid(int bid);
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
