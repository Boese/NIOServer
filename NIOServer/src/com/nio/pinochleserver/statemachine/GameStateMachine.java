package com.nio.pinochleserver.statemachine;

import java.util.ArrayList;
import java.util.List;

import com.nio.pinochleserver.Player;
import com.nio.pinochleserver.statemachine.card.Card;
import com.nio.pinochleserver.statemachine.card.Position;
import com.nio.pinochleserver.statemachine.card.Suit;
import com.nio.pinochleserver.statemachine.states.PinochleGameState;

//StateMachine for Pinochle Game
/*
 * Implements State Pattern
 * author: Chris Boese
 */
public class GameStateMachine {
	
	//** CONSTANTS **
	private static final int numberOfCards = 48;
	private static final int scoreToWin = 150;
	private static final boolean subtractScoreIfLose = true;
	private static final boolean trumpMarriageWorthDouble = true;
	private static final boolean winByTakingBidOnly = true;
	private static final boolean redeal5Nines = true;
	//***************	
	
	//** States
	private PinochleGameState Start;	// Less than 4 players
	private PinochleGameState PreRound; //Bidding, passing, melding
	private PinochleGameState Round; // playing hand, Check for winner
	private PinochleGameState GameOver; //Winner determined
	
	//** Class Variables
	private List<Player> players;
	private int team1Score;
	private int team2Score;
	private int currentBid;
	private Position currentTurn; //enum position
	private Suit currentTrump; //enum Suit
	
	PinochleGameState currentState = Start;
	
	//** Constructor
	public GameStateMachine() {
		this.players = new ArrayList<Player>(4);
		this.team1Score = 0;
		this.team2Score = 0;
		this.currentBid = 0;
		this.currentTurn = Position.North;
		this.currentTrump = Suit.Hearts;
	}
	
	//** State Interface Methods
	public List<Card> deal() {
		return currentState.deal();
	}
	public boolean checkForNines() {
		return currentState.checkForNines();
	}
	public void bid(Player player, int bid) {
		currentState.bid(player, bid);
	}
	public Player whoWonBid() {
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
	public void addPlayer(Player p) throws Exception {
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
	
}
