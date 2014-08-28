package com.nio.pinochleserver.enums;

import java.util.List;

import com.nio.pinochleserver.player.Player;

public class Move {
	private int bid;
	private Position trump;
	private List<Card> cards;
	private Card card;
	private Player player;
	
	public Move() {}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getBid() {
		return bid;
	}
	public void setBid(int bid) {
		this.bid = bid;
	}
	public Position getTrump() {
		return trump;
	}
	public void setTrump(Position trump) {
		this.trump = trump;
	}
	public List<Card> getCards() {
		return cards;
	}
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
}
