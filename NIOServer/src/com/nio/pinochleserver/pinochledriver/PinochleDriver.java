package com.nio.pinochleserver.pinochledriver;

import java.io.IOException;
import java.util.Scanner;

import naga.NIOService;
import naga.NIOSocket;

import com.nio.pinochleserver.statemachine.GameStateMachine;

public class PinochleDriver {
	
	public boolean winner = false;
	//Execute in order
	//Loop until checkForWinner returns true
	//
	 GameStateMachine p;
	 
	 
	 public PinochleDriver() throws Exception {
		// Start up the service.
		 p = new GameStateMachine();
         NIOService service = new NIOService();

         // Open our socket.
         NIOSocket socket = service.openSocket("localhost", 5218);
         p.addPlayer(socket);
         p.addPlayer(socket);
         p.addPlayer(socket);
         p.addPlayer(socket);
	 }
	
	public void startGame() {
		Scanner s = new Scanner(System.in);
		String winner = "";
		while(winner != "quit") {
			p.deal();
			System.out.println(p.getCurrentTurn() + " : ");
			s.nextLine();
		}
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
}
