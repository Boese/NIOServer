package com.nio.pinochleserver.pinochlegames;

import java.util.ArrayList;
import java.util.List;
import naga.NIOSocket;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.JSONConvert;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.player.Player;

import com.nio.pinochleserver.states.*;

public class Pinochle implements GameStateSubject, iPinochleState{
	
	//** Class Variables
	List<Player> players;
	int team1Score = 0;
	int team2Score = 0;
	Position currentTurn;
	Suit currentTrump;
	Request currentRequest;
	JSONConvert jConvert;
	Object lastMove;
	String currentMessage;
	
	//** iPinochleStates
	iPinochleState Start;
	iPinochleState Deal;
	iPinochleState Bid;
	iPinochleState Trump;
	iPinochleState Pass;
	iPinochleState Meld;
	iPinochleState Pause;
	iPinochleState Gameover;
	iPinochleState Round;
	
	//** Observers
	List<GameStateObserver> pinochleGameObservers = new ArrayList<GameStateObserver>();
	PinochleMessage pinochleMessage = new PinochleMessage();
	
	//** Current iPinochleState
	iPinochleState currentState = Start;
	
	//** Default Constructor
	public Pinochle() {
		players = new ArrayList<Player>(4);
		currentTurn = Position.North;
		currentTrump = null;
		currentRequest = Request.Null;
		jConvert = new JSONConvert();
		lastMove = new Object();
		currentMessage = "";
		
		Start = new Start(this);
		Deal = new Deal(this);
		Bid = new Bid(this);
		Trump = new Trump(this);
		Pass = new Pass(this);
		Meld = new Meld(this);
		Pause = new Pause(this);
		Gameover = new Gameover(this);
		Round = new Round(this);
	}
	
	// Set current state
	public void setState(final iPinochleState state) {
		this.currentState = state;
	}
	
	//** Play function will call current state Play()
	public void Play(JSONObject response) {
		if(!gameFull())
			setState(Pause);
		currentState.Play(response);
	}
	
	//Observer methods
	@Override
	public void registerObserver(GameStateObserver observer) {
		pinochleGameObservers.add(observer);
	}

	@Override
	public void removeObserver(GameStateObserver observer) {
		pinochleGameObservers.remove(observer);
	}
	
	@Override
	public void gameOver() {
		pinochleMessage.update(this);
		for(GameStateObserver observer : pinochleGameObservers)
			observer.gameOver();
	}
	@Override
	public void notification() {
		pinochleMessage.update(this);
		for(GameStateObserver observer : pinochleGameObservers)
			observer.notifyAll(pinochleMessage.getPinochleMessage().toString());
	};

	@Override
	public void playerRequest() {
		pinochleMessage.update(this);
		for (GameStateObserver observer : pinochleGameObservers)
			observer.request(getCurrentSocket(), pinochleMessage.getPinochleMessage(getCurrentSocket()).toString());
	}
	
	@Override
	public void playerNotification(NIOSocket socket, String msg) {
		for(GameStateObserver observer : pinochleGameObservers)
			observer.notifyPlayer(socket, msg);
	}
		
	// Helper methods
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
		if(players.size() <= 3) {
			players.add(p);
			playerNotification(socket, "**WELCOME TO PINOCHLE**");
			playerNotification(socket, "You are player " + position + " on team " + teamNum);
		}
		else
			throw new Exception("FourHandedPinochle Full");
	}
		
	public void removePlayer(NIOSocket socket) {
		for (Player player : players) {
			if(player.getSocket() == socket) {
				players.remove(player);
				currentRequest = Request.Null;
				currentMessage = "Player " + player.getPosition() + " just quit...";
				notification();
				break;
			}
		}
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
	
	public List<Player> getPlayers() {
		return this.players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
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

	public Request getCurrentRequest() {
		return currentRequest;
	}

	public void setCurrentRequest(Request currentRequest) {
		this.currentRequest = currentRequest;
	}

	public JSONConvert getjConvert() {
		return jConvert;
	}

	public void setjConvert(JSONConvert jConvert) {
		this.jConvert = jConvert;
	}

	public Object getLastMove() {
		return lastMove;
	}

	public void setLastMove(Object lastMove) {
		this.lastMove = lastMove;
	}

	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}

	public iPinochleState getStartState() {
		return Start;
	}

	public void setStart(iPinochleState start) {
		Start = start;
	}

	public iPinochleState getDealState() {
		return Deal;
	}

	public void setDeal(iPinochleState deal) {
		Deal = deal;
	}

	public iPinochleState getBidState() {
		return Bid;
	}

	public void setBid(iPinochleState bid) {
		Bid = bid;
	}

	public iPinochleState getTrumpState() {
		return Trump;
	}

	public void setTrump(iPinochleState trump) {
		Trump = trump;
	}

	public iPinochleState getPassState() {
		return Pass;
	}

	public void setPass(iPinochleState pass) {
		Pass = pass;
	}

	public iPinochleState getMeldState() {
		return Meld;
	}

	public void setMeld(iPinochleState meld) {
		Meld = meld;
	}

	public iPinochleState getPauseState() {
		return Pause;
	}

	public void setPause(iPinochleState pause) {
		Pause = pause;
	}

	public iPinochleState getGameoverState() {
		return Gameover;
	}

	public void setGameover(iPinochleState gameover) {
		Gameover = gameover;
	}

	public iPinochleState getRoundState() {
		return Round;
	}

	public void setRound(iPinochleState round) {
		Round = round;
	}

	public List<GameStateObserver> getPinochleGameObservers() {
		return pinochleGameObservers;
	}

	public void setPinochleGameObservers(
			List<GameStateObserver> pinochleGameObservers) {
		this.pinochleGameObservers = pinochleGameObservers;
	}

	public PinochleMessage getPinochleMessage() {
		return pinochleMessage;
	}

	public void setPinochleMessage(PinochleMessage pinochleMessage) {
		this.pinochleMessage = pinochleMessage;
	}

	public iPinochleState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(iPinochleState currentState) {
		this.currentState = currentState;
	}
}
