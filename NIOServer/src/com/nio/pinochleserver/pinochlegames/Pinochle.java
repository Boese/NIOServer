package com.nio.pinochleserver.pinochlegames;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import naga.NIOSocket;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.GameResponse;
import com.nio.pinochleserver.enums.Move;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.helperfunctions.CalculateMeld;
import com.nio.pinochleserver.player.Player;

public class Pinochle implements iPinochleState {
	
	//** CONSTANTS **
	private static final int scoreToWin = 150;
	private static final boolean subtractScoreIfLose = true;
	private static final boolean trumpMarriageWorthDouble = true;
	private static final boolean winByTakingBidOnly = true;
	private static final boolean redeal5Nines = true;
	//***************	
	
	//** Class Variables
	private List<Player> players = new ArrayList<Player>(4);
	private int team1Score = 0;
	private int team2Score = 0;
	private int currentBid = 0;
	private Position currentTurn = Position.North;
	private Position bidTurn = Position.North;
	private Suit currentTrump = null;
	private Position highestBidder = null;
	private String playerResponse = "";
	private List<String> broadcastResponse = new ArrayList<String>();
	
	//** iPinochleStates
	private iPinochleState Start = new Start();
	private iPinochleState Deal = new Deal();
	private iPinochleState Bid = new Bid();
	private iPinochleState Trump = new Trump();
	private iPinochleState Pass = new Pass();
	private iPinochleState Meld = new Meld();
	private iPinochleState Pause = new Pause();
	private iPinochleState Gameover = new Gameover();
	//		iPinochleState PlayRound
	
	//** Current iPinochleState
	private iPinochleState currentState = Start;
	
	//** Default Constructor
	public Pinochle() {}
	
	//** Play function will call current state Play()
	@Override
	public GameResponse Play(String move) {
		if(!gameFull())
			setState(Pause);
		broadcastResponse.clear();	//Clear out broadcast list
		playerResponse = "";	//Clear out playerResponse
		return currentState.Play(move);
	}
	
	//** set current state to Pause
	public GameResponse Pause(Player p) {
		broadcastResponse.clear();	//Clear out broadcast list
		playerResponse = "";	//Clear out playerResponse
		setState(Pause);
		return GameResponse.Pause;
	}
	
	private void setState(final iPinochleState state) {
		this.currentState = state;
	}
	
	// Get broadcast or player response
	public String getCurrentResponse() {
		return playerResponse;
	}
	
	public List<String> getBroadcastResponse() {
		return broadcastResponse;
	}
	
	//** Internal private iPinochleState classes 
	private class Start implements iPinochleState {

		@Override
		public GameResponse Play(String move) {
			setState(Deal);
			for (int i=0;i<4;i++) {
				broadcastResponse.add("StartGame");
			}
			return GameResponse.Broadcast;
		}
	}
	
	private class Deal implements iPinochleState {
		@Override
		public GameResponse Play(String move) {
			deal();
			for (Player player : players) {
				broadcastResponse.add(player.toCardString());
			}
			if(!checkForNines()) {
				setState(Bid);
			}
			else {
				for (int i=0;i<4;i++) {
					broadcastResponse.add("5 Nines redeal");
				}
			}
			return GameResponse.Broadcast;
		}
		
		private void deal() {
			final List<Suit> suits = asList(Suit.Hearts,Suit.Diamonds,Suit.Spades,Suit.Clubs);
			final List<Face> faces = asList(Face.Nine,Face.Jack,Face.Queen,Face.King,Face.Ten,Face.Ace);
			
			List<Card> deck = new ArrayList<Card>(48);
			
			// Fill new Pinochle deck
			for (int i = 0; i < 2; i++) {	// 2 of each card *
				for (Suit suit : suits) {	// 4 of each suit *
					for (Face face : faces) {	// 6 of each face = 48 cards
						deck.add(new Card(suit,face));
					}
				}
			}
			// Shuffle deck
			Collections.shuffle(deck);
			
			// Deal out 12 cards to each player
			int from = 0;
			int to = 12;
			for (Player player : players) {
				player.setCards(deck.subList(from, to));
				from += 12;
				to += 12;
			}
		}
		
		private boolean checkForNines() {
			boolean result = false;
			for (Player p : players) {
				if(p.containsFiveNinesNoMeld())
					result = true;
			}
			return result;
		}
	}
	
	private class Bid implements iPinochleState {
		private List<Position> bidders;
		private ListIterator<Position> biddersIterator;
		private boolean startBid = true;
		
		@Override
		public GameResponse Play(String move) {
			if(startBid) {
				startBid(); 
				startBid = false;
			}
			if(move != null) {
				int newBid = Integer.parseInt(move);
				boolean result = bid(newBid);
				if(result && highestBidder != null) {
					startBid = true;
					setState(Trump);
					for(int i=0;i<4;i++) {
						int num = i+1;
						playerResponse += "\t" + Suit.Hearts.getNext(i) + " - " + num;
					}
					playerResponse += "\n\nSelect trump (1-4) : ";
					return GameResponse.Player;
				}
				else if(result && highestBidder == null) {
					startBid = true;
					setState(Deal);
					for (int i=0;i<4;i++) {
						broadcastResponse.add("everyone passed redeal");
					}
				}
				return GameResponse.Broadcast;
				
			}
			else {
				playerResponse = "CurrentBid: " + currentBid + "\n" + currentTurn + " bid :";
				return GameResponse.Player;
			}
		}
		
		private void incTurn() {
			bidTurn = bidTurn.getNext(1);
		}
		
		private void startBid() {
			bidders = new ArrayList<Position>();
			for(int i=0;i<4;i++)
				bidders.add(bidTurn.getNext(i));
			biddersIterator = bidders.listIterator();
			currentTurn = bidTurn;
			currentBid = 0;
			highestBidder = null;
		}
		
		private boolean bid(int bid) {
			Position currentPosition = biddersIterator.next();
			
			// bidder passed remove bidder
			if(bid == 0) {
				biddersIterator.remove();
			}
			// bidder bid
			else if(bid > currentBid) {
				currentBid = bid;
				getPlayer(currentPosition).setBid(bid);
				highestBidder = currentPosition;
			}
			// bid not high enough prompt again
			else {
				currentTurn = biddersIterator.previous();
				return false;
			}
			
			//one bidder left and at least one bid
			if(bidders.size() == 1 && currentBid != 0) {
				currentTurn = highestBidder;
				incTurn();
				return true;
			}
			//everyone passed
			if(bidders.size() == 0) {
				currentTurn = bidTurn;
				incTurn();
				return true;
			}
			
			//Check if iterator is at end of bidders
			if(!biddersIterator.hasNext())
				biddersIterator = bidders.listIterator();
			
			//set the current turn to next available bidder
			currentTurn = biddersIterator.next();
			biddersIterator.previous();
			return false;
		}
	}
	
	private class Trump implements iPinochleState {

		@Override
		public GameResponse Play(String move) {
			int selection = Integer.parseInt(move);
			if(selection > 0 && selection < 5) {
				currentTrump = (Suit.Hearts.getNext(selection-1));
				setState(Pass);
				for (int i=0;i<4;i++) {
					broadcastResponse.add("Trump is " + currentTrump);
				}
				return GameResponse.Broadcast;
			}
			else {
				for(int i=0;i<4;i++) {
					int num = i+1;
					playerResponse += "\t" + Suit.Hearts.getNext(i) + " - " + num + "\n";
				}
				playerResponse += "\n\nSelect trump (1-4) : ";
				return GameResponse.Player;
			}
		}
	}
	
	private class Pass implements iPinochleState {

		@Override
		public GameResponse Play(String move) {
			setState(Meld);
			for (int i=0;i<4;i++) {
				broadcastResponse.add("** PASSING CARDS **");
			}
			return GameResponse.Broadcast;
		}
		
		private void passCards(Player from, Player to, List<Card> cards) {
			List<Card> temp = to.addCardsToCurrent(cards);
			to.setCards(temp);
			temp = from.removeCardsFromCurrent(cards);
			from.setCards(temp);
		}
		
	}
	
	private class Meld implements iPinochleState {

		@Override
		public GameResponse Play(String move) {
			for (Player player : players) {
				broadcastResponse.add("Meld : " + new CalculateMeld(currentTrump, player.getCurrentCards()).calculate());
			}
			setState(Gameover);
			return GameResponse.Broadcast;
		}
		
	}
	
	private class Pause implements iPinochleState {

		@Override
		public GameResponse Play(String move) {
			for (int i=0;i<4;i++) {
				broadcastResponse.add("Round is restarting because a player left");
			}
			setState(Start);
			return GameResponse.Pause;
		}
	}
	
	private class Gameover implements iPinochleState {

		@Override
		public GameResponse Play(String move) {
			return GameResponse.Gameover;
		}
	}
	
	//** Helper Methods
		
	private Position findNextAvailablePosition() {
		List<Position> availPositions = new ArrayList<Position>();
		availPositions.add(Position.North);
		availPositions.add(Position.East);
		availPositions.add(Position.South);
		availPositions.add(Position.West);
		for (Player player : players) {
			availPositions.remove(player.getPosition());
		}
		return availPositions.get(0);
	}
		
	public void addPlayer(NIOSocket socket) throws Exception {
		Position position = findNextAvailablePosition();
		int teamNum = 1;
		if(position.equals(Position.East) || position.equals(Position.West))
			teamNum = 2;
		Player p = new Player(position,teamNum,socket);
		if(this.players.size() <= 3)
			this.players.add(p);
		else
			throw new Exception("FourHandedPinochle Full");
	}
		
	public boolean removePlayer(NIOSocket socket) throws Exception {
		boolean success = false;
		if(this.players.size() > 0) {
			for (Player player : players) {
				if(player.getSocket() == socket) {
					success = true;
					players.remove(player);
					break;
				}
			}
		}
		return success;
	}
		
	public Player getPlayer(Position position) {
		Player tempPlayer = null;
		for (Player player : players) {
			if(player.getPosition() == position)
				tempPlayer = player;
		}
		return tempPlayer;
	}
		
	public Position getPosition(NIOSocket socket) {
		Position tempPosition = null;
		for (Player player : players) {
			if(player.getSocket() == socket)
				tempPosition = player.getPosition();
		}
		return tempPosition;
	}
		
	public boolean gameFull() {
		boolean full = false;
		if(players.size() == 4) {
			full = true;
		}
		return full;
	}
	
	public NIOSocket getCurrentSocket() {
		return getPlayer(currentTurn).getSocket();
	}
}
