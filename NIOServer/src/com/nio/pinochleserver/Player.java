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
	private int currentBid;
	private NIOSocket socket;
	
	public Player(Position position, int team, NIOSocket socket) {
		this.position=position;
		this.team = team;
		this.currentCards = new ArrayList<Card>();
		this.currentMeld = 0;
		this.currentBid = 0;
		this.socket = socket;
	}
	
	public void setSocket(NIOSocket socket) {
		this.socket = socket;
	}
	
	public NIOSocket getSocket() {
		return this.socket;
	}
	
	public void setBid(int bid) {
		this.currentBid = bid;
	}
	
	public int getBid() {
		return this.currentBid;
	}
	
	public void setMeld(int meld) {
		this.currentMeld = meld;
	}
	
	public void setCards(List<Card> newCards) {
		this.currentCards = newCards;
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
