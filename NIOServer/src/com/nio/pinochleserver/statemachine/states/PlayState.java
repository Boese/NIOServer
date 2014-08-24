package com.nio.pinochleserver.statemachine.states;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nio.pinochleserver.Player;
import com.nio.pinochleserver.statemachine.Game;
import com.nio.pinochleserver.statemachine.card.Card;
import com.nio.pinochleserver.statemachine.card.Face;
import com.nio.pinochleserver.statemachine.card.Position;
import com.nio.pinochleserver.statemachine.card.Suit;

public class PlayState implements PinochleGameState {

	private Game game;
	
	public PlayState(Game gamestatemachine) {
		this.game=gamestatemachine;
	}
	@Override
	public void deal() {
		final List<Suit> suits = asList(Suit.Hearts,Suit.Diamonds,Suit.Spades,Suit.Hearts);
		final List<Face> faces = asList(Face.Nine,Face.Jack,Face.Queen,Face.King,Face.Ten,Face.Ace);
		
		List<Card> deck = new ArrayList<Card>(48);
		
		// Fill new Pinochle deck
		for (int i = 0; i < 2; i++) {	// 2 of each card *
			for (Suit suit : suits) {	// 4 of each suit *
				for (Face face : faces) {	// 6 of each face = 48 cards
					deck.add(new Card(suit,face));
				}
			}
		}
		
		// Shuffle deck
		Collections.shuffle(deck);
		
		// Deal out 12 cards to each player
		int from = 0;
		int to = 11;
		for (Player player : game.getPlayers()) {
			player.setCards(deck.subList(from, to));
			from += 12;
			to += 12;
		}
	}

	@Override
	public boolean checkForNines() {
		boolean result = false;
		for (Player p : game.getPlayers()) {
			if(p.containsFiveNines())
				result = true;
		}
		return result;
	}
	
	@Override
	public void startBid() {
		game.setBidCounter(); //initialize to 3
	}

	// if bid == 0, player passes, increment turn
	@Override
	public int bid(Player from, int bid) {
		if(bid != 0) {
			game.setCurrentBid(bid);
			game.setHighestBidder(game.getCurrentTurn());
		}
		else
			game.decBidCounter();
		game.setCurrentTurn(game.getCurrentTurn().getNext());
		return game.getCurrentbid();
	}

	@Override
	public Position whoWonBid() {
		return game.getHighestBidder();
	}

	@Override
	public void passCards(Player from, Player to, List<Card> cards) {
		List<Card> tempCardsTo = to.getCurrentCards();
		for (Card card : tempCardsTo) {
			tempCardsTo.add(card);
		}
		to.setCards(cards);
	}

	@Override
	public int calculateMeld(Suit trump, List<Card> cards) {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public boolean possibleToMakeBid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean playCard(Player from, Card c) {
		System.out.println("Player : " + from.getPosition() + c.toString());
		game.setCurrentTurn(game.getCurrentTurn().getNext());
		return true;
	}

	@Override
	public Player winHand() {
		return game.getPlayers().get(0);
	}

	@Override
	public boolean checkForWinner() {
		boolean winner = false;
		if(game.getTeam1Score() >= 150)
			winner = true;
		if(game.getTeam2Score() >= 150)
			winner = true;
		return winner;
	}
	

}
