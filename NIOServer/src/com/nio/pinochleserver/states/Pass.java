package com.nio.pinochleserver.states;

import java.util.List;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;
import com.nio.pinochleserver.player.Player;

public class Pass implements iPinochleState {
	Pinochle mP;
	public Pass(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("*** PASSING CARDS ***");
		mP.notification();
		mP.setState(mP.getMeldState());
		mP.Play(null);
	}
	
	@SuppressWarnings("unused")
	private boolean passCards(Player from, Player to, List<Card> cards) {
		boolean result = false;
		try {
			if(cards.size() != 4)
				throw new Exception("incorrect number of cards");
			to.addCardsToCurrent(cards);
			from.removeCardsFromCurrent(cards);
			result = true;
		}
		catch(Exception e) {}
		return result;
	}
	
}
