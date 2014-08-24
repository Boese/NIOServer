package com.nio.pinochleserver.statemachine;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import naga.NIOSocket;

import com.nio.pinochleserver.Player;
import com.nio.pinochleserver.statemachine.card.Card;
import com.nio.pinochleserver.statemachine.card.Position;
import com.nio.pinochleserver.statemachine.card.Suit;
import com.nio.pinochleserver.statemachine.states.PinochleGameState;
import com.nio.pinochleserver.statemachine.states.PlayState;

//StateMachine for Pinochle Game
/*
 * Implements State Pattern
 * author: Chris Boese
 */
public class Game {
	
	//** CONSTANTS **
	private static final int numberOfCards = 48;
	private static final int scoreToWin = 150;
	private static final boolean subtractScoreIfLose = true;
	private static final boolean trumpMarriageWorthDouble = true;
	private static final boolean winByTakingBidOnly = true;
	private static final boolean redeal5Nines = true;
	//***************	
	
	//** States
	private PinochleGameState StartState;	// Less than 4 players
	private PinochleGameState PlayState;
	private PinochleGameState GameOverState; //Winner determined
	
	//** Class Variables
	private List<Player> players;
	private int team1Score;
	private int team2Score;
	private int currentBid;
	private Position currentTurn; //enum position
	private Suit currentTrump; //enum Suit
	private int bidCounter;
	private Position highestBidder;

	PinochleGameState currentState = PlayState;
	
	//** Constructor
	public Game() {
		this.players = new ArrayList<Player>(4);
		this.team1Score = 0;
		this.team2Score = 0;
		this.currentBid = 0;
		this.currentTurn = Position.North;
		this.currentTrump = Suit.Hearts;
		this.PlayState = new PlayState(this);
		this.currentState = PlayState;
		this.highestBidder = null;
	}
	
	//** State Interface Methods
	public void deal() {
		currentState.deal();
	}
	public boolean checkForNines() {
		return currentState.checkForNines();
	}
	public void startBid() {
		currentState.startBid();
	}
	public void bid(Player from, int bid) {
		currentState.bid(from, bid);
	}
	public Position whoWonBid() {
		return currentState.whoWonBid();
	}
	public void passCards(Player from, Player to, List<Card> cards) {
		currentState.passCards(from, to, cards);
	}
	public int calculateMeld(Suit trump, List<Card> cards) {
		return currentState.calculateMeld(trump, cards);
	}
	public boolean possibleToMakeBid() {
		return currentState.possibleToMakeBid();
	}
	public boolean playCard(Player from, Card c) {
		return currentState.playCard(from, c);
	}
	public Player winHand() {
		return currentState.winHand();
	}
	public boolean checkForWinner() {
		return currentState.checkForWinner();
	}
	
	
	//** Helper Methods
	public int getBidCounter() {
		return bidCounter;
	}
	public void setBidCounter() {
		bidCounter = 0;
	}
	public void decBidCounter() {
		this.bidCounter--;
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
			throw new Exception("Game Full");
	}
	
	public void removePlayer(Player p) throws Exception {
		if(this.players.size() > 0) {
			if(this.players.contains(p))
				this.players.remove(p);
			else
				throw new Exception("Player doesn't exist");
		}
		else
			throw new Exception("Game is empty");
	}
	
	public boolean gameFull() {
		boolean full = false;
		if(this.players.size() == 4) {
			full = true;
		}
		return full;
	}
	
	public PinochleGameState getCurrentState() {
		return this.currentState;
	}
	
	public void setState(PinochleGameState state) {
		this.currentState = state;
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}
	
	public void setCurrentBid(int bid) {
		this.currentBid = bid;
	}
	
	public int getCurrentbid() {
		return this.currentBid;
	}

	public Position getHighestBidder() {
		return highestBidder;
	}

	public void setHighestBidder(Position highestBidder) {
		this.highestBidder = highestBidder;
	}

	public int getTeam1Score() {
		return team1Score;
	}

	public void setTeam1Score(int team1Score) {
		this.team1Score = team1Score;
	}

	public int getTeam2Score() {
		return team2Score;
	}

	public void setTeam2Score(int team2Score) {
		this.team2Score = team2Score;
	}

	public Position getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(Position currentTurn) {
		this.currentTurn = currentTurn;
	}

	public Suit getCurrentTrump() {
		return currentTrump;
	}

	public void setCurrentTrump(Suit currentTrump) {
		this.currentTrump = currentTrump;
	}
	
	public Player getPlayer(Position position) {
		Player tempPlayer = null;
		for (Player player : players) {
			if(player.getPosition() == position)
				tempPlayer = player;
		}
		return tempPlayer;
	}
}
