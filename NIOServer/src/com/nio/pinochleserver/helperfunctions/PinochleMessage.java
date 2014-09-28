package com.nio.pinochleserver.helperfunctions;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.PinochleState;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.player.Player;

public class PinochleMessage {
	int team1Score;
	int team2Score;
	Position currentTurn;
	Request currentRequest;
	PinochleState currentState;
	String currentMessage;
	List<Card> cards;
	Position myPosition;
	Boolean myTurn;
	
	public PinochleMessage() {}
	
	public PinochleMessage(Pinochle pin) {
		team1Score = pin.getTeam1Score();
		team2Score = pin.getTeam2Score();
		currentTurn = pin.getCurrentTurn();
		currentRequest = pin.getCurrentRequest();
		currentState = pin.getPinochleState();
		currentMessage = pin.getCurrentMessage();
	}
	
	public String update(Player pl) {
		cards = pl.getCurrentCards();
		myPosition = pl.getPosition();
		if(myPosition == currentTurn)
			myTurn = true;
		else
			myTurn = false;
		
		ObjectMapper mapper = new ObjectMapper();
		String result = "";
		
		try {
			result = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
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

	public Request getCurrentRequest() {
		return currentRequest;
	}

	public void setCurrentRequest(Request currentRequest) {
		this.currentRequest = currentRequest;
	}
	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public Position getMyPosition() {
		return myPosition;
	}

	public void setMyPosition(Position myPosition) {
		this.myPosition = myPosition;
	}

	public Boolean getMyTurn() {
		return myTurn;
	}

	public void setMyTurn(Boolean myTurn) {
		this.myTurn = myTurn;
	}

	public PinochleState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(PinochleState currentState) {
		this.currentState = currentState;
	}
}
