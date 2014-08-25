package com.nio.pinochleserver.pinochlegames;

import java.util.List;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.GameResponse;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.player.Player;

/*
 *
 * GAME:
 * 		public String play(String move); //Keeps track of state of game
 *
 * PREROUND:
 * 		
 * 		void deal() //deal cards
 * 		boolean checkForNines()		// check if anyone got 5 nines
 * 		void bid(player p, int bid)		// add bid to player
 *		possibly redeal()
 * 		player whoWonBid() //return player who won bid, null if everyone passed
 * 		void passCards(player from, player to, List<Card> cards)	// pass 4 cards from player to player
 * 		int calculateMeld(Suit trump, List<Card>)	//calculateMeld from List<Card>
 * 		boolean possibleToMakeBid()		// return true if team can make bid
 * 
 */

/*
 * ROUND:
 * 		static int cards played = 0;
 * 		
 * 		boolean playCard(player from, Card c) //true if ok, false if invalid move
 * 		player winHand() //return player who won round
 * 		boolean checkForWinner() //return true if winner 
 * 		
 * 
 */

public interface PinochleGame {
	/*
	 * GAME:
	 */
	public GameResponse play(String move);
	
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