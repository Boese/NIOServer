package com.nio.pinochleserver.pinochledriver;

public class PinochleDriver {
	
	//Execute in order
	//Loop until checkForWinner returns true
	//
	// PinochleGameStateMachine p = new PinochleGameStateMachine
	// PinochleDriver()
	
	
	/*
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
}
