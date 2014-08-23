package com.nio.pinochleserver;

import java.util.ArrayList;
import java.util.List;

import com.nio.pinochleserver.statemachine.card.Card;
import com.nio.pinochleserver.statemachine.card.Face;
import com.nio.pinochleserver.statemachine.card.Position;

import naga.NIOSocket;

public class Player {
	private Position position; // enum position
	private int team; //team 1 or team 2
	private List<Card> currentCards; //current cards List<enum cards>
	private int currentMeld; //current meld score for player
	private NIOSocket socket;
	
	public Player(Position position, int team, NIOSocket socket) {
		this.position=position;
		this.team = team;
		this.currentCards = new ArrayList<Card>();
		this.currentMeld = 0;
		this.socket = socket;
	}
	
	public void updateMeld(int meld) {
		this.currentMeld = meld;
	}
	
	public void updateCards(List<Card> newCards) {
		this.currentCards = newCards;
	}
	
	public NIOSocket getSocket() {
		return this.socket;
	}
	
	public int getCurrentMeld() {
		return this.currentMeld;
	}
	
	public List<Card> getCurrentCards() {
		return this.currentCards;
	}
	
	public Position getPosition() {
		return this.position;
	}
	
	public int getTeam() {
		return this.team;
	}
	
	public boolean containsFiveNines() {
		int numberOfNines = 0;
		for (Card card : currentCards) {
			if(card.face.equals(Face.Nine))
				numberOfNines++;
		}
		
		return (numberOfNines < 5 ? false : true);
	}
}
