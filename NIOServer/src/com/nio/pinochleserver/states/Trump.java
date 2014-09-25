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
		Suit move = null;
		move = mP.getjConvert().getTrumpFromJSON(response);
		if(move == null) {
			mP.setCurrentRequest(Request.Trump);
			mP.playerRequest();
		}
		else {
			mP.setCurrentTrump(move);
			mP.setLastMove(move);
			mP.setCurrentMessage("Trump is " + mP.getCurrentTrump());
			mP.notification();
			mP.setState(mP.getPassState());
			mP.Play(null);
		}
	}
}
