package com.nio.pinochleserver.pinochledriver;

import java.util.Scanner;

import naga.NIOService;
import naga.NIOSocket;

import com.nio.pinochleserver.statemachine.FourHandedPinochle;

public class PinochleDriver {
	
	public boolean winner = false;
	//Execute in order
	//Loop until checkForWinner returns true
	//
	 FourHandedPinochle p;
	 
	 
	 public PinochleDriver() throws Exception {
		// Start up the service.
		 p = new FourHandedPinochle();
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
			do {
				
					// deal and check for Nines
					do {
					p.deal();
					System.out.println(p.getPlayer(p.getCurrentTurn()));
						if(p.checkForNines())
							System.out.println("A player got 5 or more Nines!");
					}while(p.checkForNines());
					
					// perform bid and loop until done
					int bid;
					p.startBid();
					do {
						System.out.println("CurrentBid : " + p.getCurrentbid());
						System.out.println(p.getCurrentTurn() + ", Enter Bid: ");
						bid = Integer.parseInt(s.nextLine());
					}while(!p.bid(bid));	// bid will return true when bidding is over
					
					
			}while(p.getHighestBidder() == null);	// redeal if everyone passed
			
			System.out.println("Winning Bidder = " + p.getHighestBidder());
			System.out.println("Bid : " + p.getCurrentbid());
			System.out.println("Team that won bid : " + p.getPlayer(p.getCurrentTurn()).getTeam());
			
			System.out.println("Pass 4 cards to Teammate " + p.getTeamMate(p.getCurrentTurn()) + ", Player " + p.getCurrentTurn());
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
