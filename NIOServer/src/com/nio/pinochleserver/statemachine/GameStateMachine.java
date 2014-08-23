package com.nio.pinochleserver.statemachine;

import java.util.ArrayList;
import java.util.List;

import com.nio.pinochleserver.Player;
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
	protected static final int numberOfCards = 48;
	protected static final int scoreToWin = 150;
	protected static final boolean subtractScoreIfLose = true;
	protected static final boolean trumpMarriageWorthDouble = true;
	protected static final boolean winByTakingBidOnly = true;
	protected static final boolean redeal5Nines = true;
	//***************	
	
	//** States
	private PinochleGameState Start;	// Less than 4 players
	private PinochleGameState PreRound; //Bidding, passing, melding
	private PinochleGameState Round; // playing hand, Check for winner
	private PinochleGameState GameOver; //Winner determined
	
	//** Class Variables
	protected List<Player> players;
	protected int team1Score;
	protected int team2Score;
	protected int currentBid;
	protected Position currentTurn; //enum position
	protected Suit currentTrump; //enum Suit
	
	PinochleGameState currentState = Start;
	
	//** Constructor
	GameStateMachine() {
		this.players = new ArrayList<Player>(4);
		this.team1Score = 0;
		this.team2Score = 0;
		this.currentBid = 0;
		this.currentTurn = Position.North;
		this.currentTrump = Suit.Hearts;
	}
	
	//** State Interface Methods
	
	
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
	
}
