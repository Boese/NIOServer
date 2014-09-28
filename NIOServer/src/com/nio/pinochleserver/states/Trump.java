package com.nio.pinochleserver.states;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;

public class Trump implements iPinochleState {
	Pinochle mP;
	public Trump(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		try {
			Suit move = mP.getMapper().readValue(response.toString(), Suit.class);
			mP.setCurrentTrump(move);
			mP.setCurrentMessage("Trump is " + mP.getCurrentTrump());
			mP.notifyObservers();
			mP.setState(mP.getPassState());
			mP.Play(null);
		} catch (Exception e) {
			mP.notifyObservers(Request.Trump);
		}
	}
}
