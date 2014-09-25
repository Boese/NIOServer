package com.nio.pinochleserver.helperfunctions;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.enums.Card;

public class CalculateMeld {

	private Suit trump;
	private List<Card> cards;
	private int score;
	
	private List<Card> run;
	private List<Card> pinochle;
	private List<Card> aces;
	private List<Card> kings;
	private List<Card> queens;
	private List<Card> jacks;
	private List<Card> dix;
	
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
		dix = asList(new Card(trump, Face.Nine));
	}
	
	// Will calculate meld based on trump and cards
	// ** if trump is null, it will not calculate based on trump
	public CalculateMeld(Suit trump, List<Card> cards) {
		this.trump = trump;
		this.cards = new ArrayList<Card>(cards);
		this.score = 0;
		initialize();
	}
	
	//**Helper Functions **
	// Will return true if first param List<Card> contains double of second param List<Card>
		public boolean containsDouble(final List<Card> cardList, final List<Card> containsList) {
			List<Card> mutableCards = new ArrayList<Card>(cardList);
			
			List<Card> doubleList = new ArrayList<Card>(containsList); doubleList.addAll(containsList);
			
			ListIterator<Card> cardIterator = doubleList.listIterator();
			
			while(cardIterator.nextIndex() < doubleList.size() && mutableCards.size() >= 0) {
				int potentialMatch = cardIterator.nextIndex();
				if(mutableCards.contains(doubleList.get(potentialMatch))) {
					mutableCards.remove(doubleList.get(potentialMatch));
					doubleList.remove(potentialMatch);
					cardIterator = doubleList.listIterator();
				}
				else
					cardIterator.next();
			}
			
			return (doubleList.size() == 0) ? true : false;
		}
		
		// Will remove only one card from constCards from mutableCards
		// returns mutable Cards
		public List<Card> removeOne(List<Card> mutableCards, final List<Card> constCards ) {
			List<Card> copyRun = new ArrayList<Card>(constCards);
			ListIterator<Card> cardsIterator = copyRun.listIterator();
			while(cardsIterator.nextIndex() < copyRun.size() && mutableCards.size() >= 0) {
				int potentialmatch = cardsIterator.nextIndex();
				if(mutableCards.contains(run.get(potentialmatch))) {
					mutableCards.remove(potentialmatch);
					copyRun.remove(potentialmatch);
					cardsIterator = copyRun.listIterator();
				}
				else
					cardsIterator.next();
			}
			return mutableCards;
		}
		// *****************************
	
	public int calculate() {
		// Initialize mutable temp list
		List<Card> temp = new ArrayList<Card>(cards);
		
		// boolean flags for run, doublerun
		boolean hasRun = false;
		boolean hasDoubleRun = false;
		
		// contains single: runs, 4 of a kind, pinochle, dix? Set flag for hasRun if true.
		if(temp.containsAll(run) && trump != null) { score += 15; hasRun = true;}
		if(temp.containsAll(aces)) score += 10;
		if(temp.containsAll(kings)) score += 8;
		if(temp.containsAll(queens)) score += 6;
		if(temp.containsAll(jacks)) score += 4;
		if(temp.containsAll(pinochle)) score += 4;
		if(temp.containsAll(dix) && trump != null) score += 1;
		
		// contains double : runs, 4 of a kind, pinochle, dix? Subtact single score if true;
		// Set flag for hasDouble run if true;
		if(containsDouble(temp, run) && trump != null){ score += 150 - 15; hasDoubleRun = true;}
		if(containsDouble(temp, aces)) score += 100 - 10;
		if(containsDouble(temp, kings)) score += 80 - 8;
		if(containsDouble(temp, queens)) score += 60 - 6;
		if(containsDouble(temp, jacks)) score += 40 - 4;
		if(containsDouble(temp, pinochle)) score += 30 - 4;
		if(containsDouble(temp, dix) && trump != null) score += 2 - 1;
		
		// Check Flags before processing Marriages. 
		// Remove run cards if true to not count duplicates. (*Only Trump)
		if(hasRun && trump != null) {
			if(hasDoubleRun) {
				temp.removeAll(run); // will remove all trump cards
			}
			else { // only remove one run
				temp = new ArrayList<Card>(removeOne(temp,run));
			}
		}
		
		// marriages & marriages in trump
		List<Card> kingsInCards = new ArrayList<Card>();
		List<Card> queensInCards = new ArrayList<Card>();
		for (Card card : temp) {
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
				if(card.suit == trump && queensInCards.get(i).suit == trump && trump != null) {
						score +=4;
				}
				else {
					score += 2;
				}
				queensInCards.remove(i);
			}
		}
		
		return score;
	}
}
