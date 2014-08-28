package com.nio.pinochleserver.player;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.CardComparator;
import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.helperfunctions.CalculateMeld;

import naga.NIOSocket;

public class Player {
	private Position position; // enum position
	private int team; //team 1 or team 2
	private List<Card> currentCards; //current cards List<enum cards>
	private int currentMeld; //current meld score for player
	private int currentBid;
	private Position teamMate;

	private NIOSocket socket;
	
	public Player(Position position, int team, NIOSocket socket) {
		this.position=position;
		this.teamMate = position.getNext(2);
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
		String hearts = "Hearts : \n";
		String diamonds = "Diamonds : \n";
		String clubs = "Clubs : \n";
		String spades = "Spades : \n";
		int i = 1;
		for (Card card : currentCards) {
			Suit s = card.suit;
			switch(s) {
			case Clubs: clubs += "\t" + i + " - " + card + "\n";
				break;
			case Diamonds: diamonds += "\t" + i + " - " + card + "\n";
				break;
			case Hearts: hearts += "\t" + i + " - " + card + "\n";
				break;
			case Spades: spades += "\t" + i + " - " + card + "\n";
				break;
			} i++;
		}
		String returnCards = hearts + spades + diamonds + clubs;
		return returnCards;
	}
	
	public List<Card> addCardsToCurrent(List<Card> cards) {
		List<Card> temp = new ArrayList<Card>(currentCards);
		temp.addAll(cards);
		return temp;
	}
	
	public List<Card> removeCardsFromCurrent(List<Card> cards) {
		List<Card> mutableCards = new ArrayList<Card>(cards);
		List<Card> copyOfCards = new ArrayList<Card>(cards);
		ListIterator<Card> cardsIterator = copyOfCards.listIterator();
		while(cardsIterator.nextIndex() < copyOfCards.size() && mutableCards.size() >= 0) {
			int potentialmatch = cardsIterator.nextIndex();
			if(mutableCards.contains(mutableCards.get(potentialmatch))) {
				mutableCards.remove(potentialmatch);
				copyOfCards.remove(potentialmatch);
				cardsIterator = copyOfCards.listIterator();
			}
			else
				cardsIterator.next();
		}
		return mutableCards;
	}
	
	public Position getTeamMate() {
		return teamMate;
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
	
	public boolean containsFiveNinesNoMeld() {
		int numberOfNines = 0;
		for (Card card : currentCards) {
			if(card.face.equals(Face.Nine))
				numberOfNines++;
		}
		
		if(numberOfNines >= 5 && new CalculateMeld(null, currentCards).calculate() == 0)
			return true;
		return false;
	}
}
