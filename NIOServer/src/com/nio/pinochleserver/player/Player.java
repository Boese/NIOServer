package com.nio.pinochleserver.player;

import java.util.ArrayList;
import java.util.List;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.CardComparator;
import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Suit;

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
	
	public String toString() {
		String player = "";
		player += "Position : " + position + "\n";
		player += "Team : " + team + "\n";
		player += "Cards : ";
		for (Card card : currentCards) {
			player += card + " , ";
		}
		player += "\n";
		player += "Socket : " + socket + "\n";
		return player;
	}
	
	public String toCardString() {
		currentCards.sort(new CardComparator());
		String hearts = "Hearts : ";
		String diamonds = "Diamonds : ";
		String clubs = "Clubs : ";
		String spades = "Spades : ";
		for (Card card : currentCards) {
			Suit s = card.suit;
			switch(s) {
			case Clubs: clubs += card + " ";
				break;
			case Diamonds: diamonds += card + " ";
				break;
			case Hearts: hearts += card + " ";
				break;
			case Spades: spades += card + " ";
				break;
			}
		}
		String returnCards = hearts + "\n" + clubs + "\n" + diamonds + "\n" + spades + "\n";
		return returnCards;
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
