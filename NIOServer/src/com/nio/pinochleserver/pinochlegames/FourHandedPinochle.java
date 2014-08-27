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
import com.nio.pinochleserver.enums.GameState;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.player.Player;

//StateMachine for Pinochle FourHandedPinochle
/*
 * Implements PinochleGame Interface
 * author: Chris Boese
 */
public class FourHandedPinochle implements PinochleGame {
	
	//** CONSTANTS **
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
	private GameState currentState;
	private String playerResponse;
	private List<String> broadcastResponse;

	//** Constructor
	public FourHandedPinochle() {
		players = new ArrayList<Player>(4);
		team1Score = 0;
		team2Score = 0;
		currentBid = 0;
		currentTurn = Position.North;
		highestBidder = null;
		bidTurn = Position.North;
		currentState = GameState.Start;
		playerResponse = "Start Game";
		broadcastResponse = new ArrayList<String>();
	}

	public String getCurrentResponse() {
		return playerResponse;
	}
	
	public List<String> getBroadcastResponse() {
		return broadcastResponse;
	}

	//** Pinochle Game Implementation
	@Override 
	public GameResponse play(String move) {
		if(!gameFull())
			currentState = GameState.Pause;
		
		GameResponse gameResponse = GameResponse.Broadcast;
		broadcastResponse.clear();	//Clear out broadcast list
		playerResponse = "";
		
			switch(currentState) {
			case Bid:
				int newBid = Integer.parseInt(move);
				boolean result = bid(newBid);
				if(result && highestBidder != null) {
					currentState = GameState.Pass;
				}
				else if(result && highestBidder == null) {
					currentState = GameState.Deal;
					for (int i=0;i<4;i++) {
						broadcastResponse.add("everyone passed redeal");
					}
				}
				else {
					playerResponse = "CurrentBid: " + currentBid + "\n" + currentTurn + " bid :";
					gameResponse = GameResponse.Player;
				}
				break;
			case Deal:
				deal();
				for (Player player : players) {
					broadcastResponse.add(player.toCardString());
				}
				currentState = GameState.CheckForNines;
				break;
			case CheckForNines:
				if(!checkForNines()) {
					currentState = GameState.Bid;
					startBid();
					playerResponse = "CurrentBid: " + currentBid + "\n" + currentTurn + " bid :";
					gameResponse = GameResponse.Player;
				}
				else {
					currentState = GameState.Deal;
					for (int i=0;i<4;i++) {
						broadcastResponse.add("5 Nines redeal");
					}
				}
				break;
			case Pause:currentState = GameState.Start;
					for (int i=0;i<4;i++) {
						broadcastResponse.add("Round is restarting because a player left");
					}
					gameResponse = GameResponse.Pause;
				break;
			case GameOver: 
				playerResponse = "gameOver";
				break;
			case Pass:
				String temp = "";
				temp += "Winning Bidder = " + highestBidder + "\n";
				temp += ("Bid : " + currentBid + "\n");
				temp += ("Team that won bid : " + getPlayer(currentTurn).getTeam() + "\n");
				temp += ("*** Starting pass between " + highestBidder + " and partner " + getTeamMate(highestBidder) + " ***\n");
				temp += "game over";
				for (int i=0;i<4;i++) {
					broadcastResponse.add(temp);
				}
				currentState = GameState.GameOver;
				break;
			case Play:
				break;
			case Start:
				for (int i=0;i<4;i++) {
					broadcastResponse.add("StartGame");
				}
					currentState=GameState.Deal;
				break;
			default:
				break;
			}
		return gameResponse;
	}
	
	@Override
	public void deal() {
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
	public void startBid() {
		bidders = new ArrayList<Position>();
		for(int i=0;i<4;i++)
			bidders.add(bidTurn.getNext(i));
		biddersIterator = bidders.listIterator();
		currentTurn = bidTurn;
		currentBid = 0;
		highestBidder = null;
	}

	// returns true if bidding is done, increment bidTurn
	@Override
	public boolean bid(int bid) {
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
	
	public NIOSocket getCurrentSocket() {
		return getPlayer(currentTurn).getSocket();
	}
	
	public Position getTeamMate(Position p) {
		return p.getNext(2);
	}
}
