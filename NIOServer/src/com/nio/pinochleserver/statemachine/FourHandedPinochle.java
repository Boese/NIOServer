package com.nio.pinochleserver.statemachine;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import naga.NIOSocket;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.pinochlegames.PinochleGame;
import com.nio.pinochleserver.player.Player;

//StateMachine for Pinochle FourHandedPinochle
/*
 * Implements PinochleGame Interface
 * author: Chris Boese
 */
public class FourHandedPinochle implements PinochleGame {
	
	//** CONSTANTS **
	private static final int numberOfCards = 48;
	private static final int scoreToWin = 150;
	private static final boolean subtractScoreIfLose = true;
	private static final boolean trumpMarriageWorthDouble = true;
	private static final boolean winByTakingBidOnly = true;
	private static final boolean redeal5Nines = true;
	//***************	
	
	//** Class Variables
	private List<Player> players;
	private int team1Score;
	private int team2Score;
	private int currentBid;
	private Position currentTurn; //enum position
	private Suit currentTrump; //enum Suit
	private Position highestBidder;
	private Position bidTurn; // keeps track of who starts bidding round
	private List<Position> bidders;
	private ListIterator<Position> biddersIterator;
	
	//** Constructor
	public FourHandedPinochle() {
		players = new ArrayList<Player>(4);
		team1Score = 0;
		team2Score = 0;
		currentBid = 0;
		currentTurn = Position.North;
		highestBidder = null;
		bidTurn = Position.North;
	}

	//** Pinochle Game Implementation
	@Override
	public void deal() {
		final List<Suit> suits = asList(Suit.Hearts,Suit.Diamonds,Suit.Spades,Suit.Hearts);
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

	@Override
	public boolean checkForNines() {
		boolean result = false;
		for (Player p : players) {
			if(p.containsFiveNines())
				result = true;
		}
		return result;
	}
	
	@Override
	public Position startBid() {
		bidders = new ArrayList<Position>();
		bidders.add(bidTurn);
		bidders.add(bidTurn.getNext(1));
		bidders.add(bidTurn.getNext(2));
		bidders.add(bidTurn.getNext(3));
		biddersIterator = bidders.listIterator();
		incTurn();
		return currentTurn;
	}

	// returns true if bidding is done, increment bidTurn
	@Override
	public boolean bid(int bid) {
		
		Position currentPosition = (Position) biddersIterator.next();
		
		// bidder passed
		if(bid == 0) {
			biddersIterator.remove();
		}
		// bidder bid
		else if(bid > currentBid) {
			currentBid = bid;
			getPlayer(currentPosition).setBid(bid);
			highestBidder = currentPosition;
		}
		// bid not high enough
		else {
			System.out.println("must enter bid higher than " + currentBid);
			currentTurn = biddersIterator.previous();
			return false;
		}
		
		//one bidder left and at least one bid
		if(bidders.size() == 1 && currentBid != 0) {
			currentTurn = highestBidder;
			return true;
		}
		//everyone passed
		if(bidders.size() == 0) {
			currentTurn = bidTurn;
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

	@Override
	public void passCards(Player from, Player to, List<Card> cards) {
		List<Card> tempCardsTo = to.getCurrentCards();
		for (Card card : tempCardsTo) {
			tempCardsTo.add(card);
		}
		to.setCards(cards);
	}

	@Override
	public int calculateMeld(Suit trump, List<Card> cards) {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public boolean possibleToMakeBid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean playCard(Player from, Card c) {
		System.out.println("Player : " + from.getPosition() + c.toString());
		currentTurn = currentTurn.getNext(1);
		return true;
	}

	@Override
	public Player winHand() {
		return null;
	}

	@Override
	public boolean checkForWinner() {
		boolean winner = false;
		if(team1Score >= 150)
			winner = true;
		if(team2Score >= 150)
			winner = true;
		return winner;
	}
	

	//** Helper Methods
	private void incTurn() {
		bidTurn = bidTurn.getNext(1);
	}
	
	public void addPlayer(NIOSocket socket) throws Exception {
		List<Position> positions = asList(Position.North,Position.East,Position.West,Position.South);
		Position position = positions.get(players.size());
		int teamNum = 1;
		if(position.equals(Position.East) || position.equals(Position.West))
			teamNum = 2;
		Player p = new Player(position,teamNum,socket);
		if(this.players.size() <= 3)
			this.players.add(p);
		else
			throw new Exception("FourHandedPinochle Full");
	}
	
	public void removePlayer(Player p) throws Exception {
		if(this.players.size() > 0) {
			if(this.players.contains(p))
				this.players.remove(p);
			else
				throw new Exception("Player doesn't exist");
		}
		else
			throw new Exception("FourHandedPinochle is empty");
	}
	
	public Player getPlayer(Position position) {
		Player tempPlayer = null;
		for (Player player : players) {
			if(player.getPosition() == position)
				tempPlayer = player;
		}
		return tempPlayer;
	}
	
	public boolean gameFull() {
		boolean full = false;
		if(this.players.size() == 4) {
			full = true;
		}
		return full;
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}
	
	public int getCurrentbid() {
		return this.currentBid;
	}

	public Position getHighestBidder() {
		return highestBidder;
	}

	public Position getCurrentTurn() {
		return currentTurn;
	}
	
	public Position getTeamMate(Position p) {
		return p.getNext(2);
	}
}
