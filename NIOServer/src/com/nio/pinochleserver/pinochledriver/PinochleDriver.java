package com.nio.pinochleserver.pinochledriver;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import naga.NIOService;
import naga.NIOSocket;

import com.nio.pinochleserver.Player;
import com.nio.pinochleserver.statemachine.Game;
import com.nio.pinochleserver.statemachine.card.Card;
import com.nio.pinochleserver.statemachine.card.Face;
import com.nio.pinochleserver.statemachine.card.Suit;

public class PinochleDriver {
	
	public boolean winner = false;
	//Execute in order
	//Loop until checkForWinner returns true
	//
	 Game p;
	 
	 
	 public PinochleDriver() throws Exception {
		// Start up the service.
		 p = new Game();
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
		String quit = "";
		while(quit != "y") {
			p.deal();
			System.out.println(p.getCurrentTurn() + " ,enter Face :");
			String face = s.nextLine();
			System.out.println(p.getCurrentTurn() + " ,enter Suit :");
			String suit = s.nextLine();
			Card c = new Card(Suit.valueOf(suit),Face.valueOf(face));
			Player player = p.getPlayer(p.getCurrentTurn());
			p.playCard(player, c);
			System.out.println("Quit? (y,n)");
			quit = s.nextLine();
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
