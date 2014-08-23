package com.nio.pinochleserver.statemachine.states;

public interface PinochleGameState {
	
	public void StartGame();
	public void StartBidding();
	public void PassCards();
	public void StartRound();
	public void CheckForWinner();
	
}


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
