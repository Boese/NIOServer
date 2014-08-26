package com.nio.pinochleserver.helperfunctions;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.enums.Card;

public class CalculateMeld {

	private Suit trump;
	private List<Card> cards;
	private int score;
	private boolean playerHasRun;
	
	private List<Card> run;
	private List<Card> pinochle;
	private List<Card> aces;
	private List<Card> kings;
	private List<Card> queens;
	private List<Card> jacks;
	private Card dix;
	
	private void initialize() {
		run = asList(
				new Card(trump, Face.Jack),
				new Card(trump, Face.Queen),
				new Card(trump, Face.King),
				new Card(trump, Face.Ten),
				new Card(trump, Face.Ace)
				);
		pinochle = asList(
				new Card(Suit.Diamonds,Face.Jack),
				new Card(Suit.Spades,Face.Queen)
				);
		aces = asList(
				new Card(Suit.Clubs,Face.Ace),
				new Card(Suit.Diamonds,Face.Ace),
				new Card(Suit.Hearts,Face.Ace),
				new Card(Suit.Spades,Face.Ace)
				);
		kings = asList(
				new Card(Suit.Clubs,Face.King),
				new Card(Suit.Diamonds,Face.King),
				new Card(Suit.Hearts,Face.King),
				new Card(Suit.Spades,Face.King)
				);
		queens = asList(
				new Card(Suit.Clubs,Face.Queen),
				new Card(Suit.Diamonds,Face.Queen),
				new Card(Suit.Hearts,Face.Queen),
				new Card(Suit.Spades,Face.Queen)
				);
		jacks = asList(
				new Card(Suit.Clubs,Face.Jack),
				new Card(Suit.Diamonds,Face.Jack),
				new Card(Suit.Hearts,Face.Jack),
				new Card(Suit.Spades,Face.Jack)
				);
		dix = new Card(trump,Face.Nine);
	}
	
	public CalculateMeld(Suit trump, List<Card> cards) {
		this.trump = trump;
		this.cards = cards;
		this.score = 0;
		this.playerHasRun = false;
		initialize();
	}
	
	public int calculate() {
		// runs, 4 of a kind, pinochle, dix
		if(cards.containsAll(run)) {score += 15; playerHasRun = true;}
		if(cards.containsAll(aces)) score += 10;
		if(cards.containsAll(kings)) score += 8;
		if(cards.containsAll(queens)) score += 6;
		if(cards.containsAll(jacks)) score += 4;
		if(cards.containsAll(pinochle)) score += 4;
		if(cards.contains(dix)) score += 1;
		
		// marriages & marriages in trump
		List<Card> kingsInCards = new ArrayList<Card>();
		List<Card> queensInCards = new ArrayList<Card>();
		for (Card card : cards) {
			if(card.face == Face.King)
				kingsInCards.add(card);
			if(card.face == Face.Queen)
				queensInCards.add(card);
		}
		
		for (Card card : kingsInCards) {
			if(queensInCards.size() == 0)
				break;
			int i = queensInCards.indexOf(new Card(card.suit,Face.Queen));
			if(i != -1) {
				if(card.suit == trump && queensInCards.get(i).suit == trump) {
					if(!playerHasRun)
						score +=4;
				}
				else {
					score += 2;
				}
				queensInCards.remove(i);
			}
		}
		
		// doubles of run, pinochle, aces, kings, queens, jacks
		List<Card> dRun = new ArrayList<Card>(run);
		List<Card> dPinochle = new ArrayList<Card>(pinochle);
		List<Card> dAces = new ArrayList<Card>(aces);
		List<Card> dKings = new ArrayList<Card>(kings);
		List<Card> dQueens = new ArrayList<Card>(queens);
		List<Card> dJacks = new ArrayList<Card>(jacks);
		
		dRun.addAll(run);
		dPinochle.addAll(pinochle);
		dAces.addAll(aces);
		dKings.addAll(kings);
		dQueens.addAll(queens);
		dJacks.addAll(jacks);
		
		if(cards.containsAll(dRun)) score += 150 - 15;
		if(cards.containsAll(dPinochle)) score += 30 - 4;
		if(cards.containsAll(dAces)) score += 100 - 10;
		if(cards.containsAll(dKings)) score += 80 - 8;
		if(cards.containsAll(dQueens)) score += 60 - 6;
		if(cards.containsAll(dJacks)) score += 40 - 4;
		
		return score;
	}
}
