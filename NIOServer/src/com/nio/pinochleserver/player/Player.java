package com.nio.pinochleserver.player;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.CardComparator;
import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.JSONConvert;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.helperfunctions.CalculateMeld;

import naga.NIOSocket;

public class Player {
	private Position position; // enum position
	private int team; //team 1 or team 2
	private List<Card> currentCards; //current cards List<enum cards>
	private Position teamMate;
	private JSONObject playerJSON;
	private JSONConvert jConvert = new JSONConvert();

	private NIOSocket socket;
	
	public void setPlayerJSON(List<Player> players, int team1Score, int team2Score, Suit currentTrump, int currentBid, Position currentTurn, Request request, String message, Object move) throws JSONException {
		playerJSON = new JSONObject();
		JSONObject scoreArray = new JSONObject();
		JSONObject playersMeldArray = new JSONObject();
		for (Player player : players) {
			int x = new CalculateMeld(currentTrump, player.getCurrentCards()).calculate();
			playersMeldArray.put("Player : " + player.getPosition(), x);
		}
		scoreArray.put("team1Score", team1Score);
		scoreArray.put("team2Score", team2Score);
		playerJSON.put("Meld", playersMeldArray);
		playerJSON.put("Score", scoreArray);
		playerJSON.put("team", team);
		playerJSON.put("Bid", currentBid);
		playerJSON.put("trump",	currentTrump);
		playerJSON.put("Current Turn", currentTurn);
		playerJSON.put("My Cards", jConvert.convertCardsToJSON(currentCards));
		playerJSON.put("request", request);
		playerJSON.put("message", message);
		playerJSON.put("LastMove", move);
	}
	
	public JSONObject getPlayerJSON() {
		return playerJSON;
	}
	
	public Player(Position position, int team, NIOSocket socket) {
		this.position=position;
		this.teamMate = position.getNext(2);
		this.team = team;
		this.currentCards = new ArrayList<Card>();
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
	
	public void setCards(List<Card> newCards) {
		this.currentCards = newCards;
		this.currentCards.sort(new CardComparator());
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
