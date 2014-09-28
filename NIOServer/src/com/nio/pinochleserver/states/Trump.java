package com.nio.pinochleserver.states;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;
import com.nio.pinochleserver.player.PlayerResponse;

public class Trump implements iPinochleState {
	Pinochle mP;
	public Trump(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		try {
			PlayerResponse playerresponse = new PlayerResponse();
			playerresponse = mP.getMapper().readValue(response.toString(), PlayerResponse.class);
			Suit move = playerresponse.getTrump();
			
			mP.setCurrentTrump(move);
			mP.setCurrentMessage(mP.getCurrentTurn() + " selected " + mP.getCurrentTrump() + " as trump!");
			mP.notifyObservers();
			mP.setState(mP.getPassState());
			mP.Play(null);
		} catch (Exception e) {
			mP.notifyObservers(Request.Trump);
		}
	}
}
