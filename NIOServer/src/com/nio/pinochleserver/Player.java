package com.nio.pinochleserver;

import java.util.ArrayList;
import java.util.List;

import naga.NIOSocket;

public class Player {
	private int position; // enum position
	private int team; //team 1 or team 2
	private List<Integer> currentCards; //current cards List<enum cards>
	private int currentMeld; //current meld score for player
	private NIOSocket socket;
	
	Player(int position, int team, NIOSocket socket) {
		this.position=position;
		this.team = team;
		this.currentCards = new ArrayList<Integer>();
		this.currentMeld = 0;
		this.socket = socket;
	}
	
	public void updateMeld(int meld) {
		this.currentMeld = meld;
	}
	
	public void updateCards(List<Integer> newCards) {
		this.currentCards = newCards;
	}
	
	public NIOSocket getSocket() {
		return this.socket;
	}
	
	public int getCurrentMeld() {
		return this.currentMeld;
	}
	
	public List<Integer> getCurrentCards() {
		return this.currentCards;
	}
	
	public int getPosition() {
		return this.position;
	}
	
	public int getTeam() {
		return this.team;
	}
}
