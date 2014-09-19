package com.nio.pinochleserver.pinochlegames;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import naga.NIOSocket;

import org.json.JSONException;
import org.json.JSONObject;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.GameResponse;
import com.nio.pinochleserver.enums.JSONConvert;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.helperfunctions.CalculateMeld;
import com.nio.pinochleserver.player.Player;

public class Pinochle implements PinochleGameSubject{
	
	//** Class Variables
	protected List<Player> players = new ArrayList<Player>(4);
	protected int team1Score = 0;
	protected int team2Score = 0;
	protected int currentBid = 0;
	protected Position currentTurn = Position.North;
	protected Position bidTurn = Position.North;
	protected Suit currentTrump = null;
	protected Position highestBidder = null;
	protected Request currentRequest = Request.Null;
	protected JSONConvert jConvert = new JSONConvert();
	protected Object lastMove = new Object();
	protected String currentMessage = "";
	
	//** iPinochleStates
	private iPinochleState Start = new Start();
	private iPinochleState Deal = new Deal();
	private iPinochleState Bid = new Bid();
	private iPinochleState Trump = new Trump();
	private iPinochleState Pass = new Pass();
	private iPinochleState Meld = new Meld();
	private iPinochleState Pause = new Pause();
	private iPinochleState Gameover = new Gameover();
	private iPinochleState Round = new Round();
	
	//** Observers
	private List<PinochleGameObserver> pinochleGameObservers = new ArrayList<PinochleGameObserver>();
	public PinochleMessage pinochleMessage = new PinochleMessage();
	
	//** Current iPinochleState
	protected iPinochleState currentState = Start;
	
	//** Default Constructor
	public Pinochle() {
		registerObserver(pinochleMessage);
	}
	
	// Set current state
	protected void setState(final iPinochleState state) {
		this.currentState = state;
	}
	
	//** Play function will call current state Play()
	public GameResponse Play(JSONObject response) {
		if(!gameFull())
			setState(Pause);
		GameResponse gameresponse = currentState.Play(response);
		notifyObservers();
		return gameresponse;
	}
	
	//** Internal private iPinochleState classes
	
	/*
	 * Start State
	 */
	private class Start implements iPinochleState {
		@Override
		public GameResponse Play(JSONObject response) {
			setState(Deal);
			currentMessage = "Starting Game!";
			return GameResponse.Broadcast;
		}
	}
	
	/*
	 * Deal State
	 */
	private class Deal implements iPinochleState {
		@Override
		public GameResponse Play(JSONObject response) {
			deal();
			currentMessage = "Dealing...";
			if(!checkForNines()) {
				setState(Bid);
			}
			else {
				currentMessage += "\nRe-dealing... One Player got 5 Nines and no meld!";
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
	
	/*
	 * Bid State
	 */
	private class Bid implements iPinochleState {
		private List<Position> bidders;
		private ListIterator<Position> biddersIterator;
		private boolean startBid = true;
		
		@Override
		public GameResponse Play(JSONObject response) {
			if(startBid) {
				startBid(); 
				startBid = false;
				currentMessage = "Starting bidding round with : " + currentTurn;
				return GameResponse.Broadcast;
			}

			int move = jConvert.getBidFromJSON(response);
			if(move != -1)
			{
				lastMove = move;
				boolean result = bid(move);
				if(result && highestBidder != null) {
					startBid = true;
					setState(Trump);
					currentMessage = "Selecting Trump...";
					return GameResponse.Broadcast;
				}
		
				if(result && highestBidder == null) {
						startBid = true;
						setState(Deal);
						currentMessage = "Everyone passed! Redeal...";
						return GameResponse.Broadcast;
					}
			}
			currentMessage = "CurrentBid : " + currentBid;
			currentRequest = Request.Bid;
			return GameResponse.Player;
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
				//getPlayer(currentPosition).setBid(bid);
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
	
	/*
	 * Trump State
	 */
	private class Trump implements iPinochleState {

		@Override
		public GameResponse Play(JSONObject response) {
			Suit move = null;
			move = jConvert.getTrumpFromJSON(response);
			if(move == null) {
				currentMessage = "";
				currentRequest = Request.Trump;
				return GameResponse.Player;
			}
			currentTrump = move;
			lastMove = move;
			setState(Pass);
			currentMessage = "Trump is " + currentTrump;
			return GameResponse.Broadcast;
		}
	}
	
	/*
	 * Pass State
	 */
	private class Pass implements iPinochleState {

		@Override
		public GameResponse Play(JSONObject response) {
			setState(Meld);
			currentMessage = "*** PASSING CARDS ***";
			return GameResponse.Broadcast;
		}
		
		private boolean passCards(Player from, Player to, List<Card> cards) {
			boolean result = false;
			try {
				if(cards.size() != 4)
					throw new Exception("incorrect number of cards");
				to.addCardsToCurrent(cards);
				from.removeCardsFromCurrent(cards);
				result = true;
			}
			catch(Exception e) {}
			return result;
		}
		
	}
	
	/*
	 * Meld State
	 */
	private class Meld implements iPinochleState {

		@Override
		public GameResponse Play(JSONObject response) {
			setState(Gameover);
			return GameResponse.Broadcast;
		}
		
	}
	
	/*
	 * Pause State
	 */
	private class Pause implements iPinochleState {

		@Override
		public GameResponse Play(JSONObject response) {
			currentMessage = "Round is restarting because a player left";
			setState(Start);
			return GameResponse.Broadcast;
		}
	}
	
	/*
	 * Round State
	 */
	private class Round implements iPinochleState {
		@Override
		public GameResponse Play(JSONObject response) {
			return null;
		}
	}
	
	/*
	 * Gameover State
	 */
	private class Gameover implements iPinochleState {

		@Override
		public GameResponse Play(JSONObject response) {
			return GameResponse.Gameover;
		}
	}
		
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
		
	// Public Helper methods
	public void addPlayer(NIOSocket socket) throws Exception {
		Position position = findNextAvailablePosition();
		int teamNum = 1;
		if(position.equals(Position.East) || position.equals(Position.West))
			teamNum = 2;
		Player p = new Player(position,teamNum,socket);
		if(players.size() <= 3)
			players.add(p);
		else
			throw new Exception("FourHandedPinochle Full");
		notifyObservers();
	}
		
	public boolean removePlayer(NIOSocket socket) throws Exception {
		boolean success = false;
		if(players.size() > 0) {
			for (Player player : players) {
				if(player.getSocket() == socket) {
					success = true;
					players.remove(player);
					break;
				}
			}
		}
		notifyObservers();
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
	
	public Player getPlayer(NIOSocket socket) {
		Player tempPlayer = null;
		for (Player player : players) {
			if(player.getSocket() == socket)
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
	
	public List<Player> getPlayer() {
		return this.players;
	}

	@Override
	public void registerObserver(PinochleGameObserver observer) {
		pinochleGameObservers.add(observer);
	}

	@Override
	public void removeObserver(PinochleGameObserver observer) {
		pinochleGameObservers.remove(observer);
	}

	@Override
	public void notifyObservers() {
		for (PinochleGameObserver observer : pinochleGameObservers) {
			observer.update(this);
		}
	}
}
